package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.constant.SortField;
import com.part2.findex.syncjob.dto.*;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.repository.SyncJobSpecification;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.part2.findex.syncjob.constant.SortDirectionConstant.ASCENDING_SORT_DIRECTION;
import static com.part2.findex.syncjob.constant.SortDirectionConstant.DESCENDING_SORT_DIRECTION;
import static com.part2.findex.syncjob.constant.SortField.jobTime;
import static com.part2.findex.syncjob.constant.SortField.targetDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexSyncOrchestratorServiceImpl implements IndexSyncOrchestratorService {
    private final OpenApiStockIndexService openApiStockIndexService;
    private final IndexInfoSyncJobService indexInfoSyncJobService;
    private final IndexDataSyncJobService indexDataSyncJobService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    @Override
    public List<SyncJobResult> syncIndexInfoWithOpenAPI() {
        Map<String, StockIndexInfoResult> indexInfosFromOpenAPI = openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI();

        // 지수정보 수정 및 등록, N+1문제 확인 필요
        List<IndexInfo> existingIndexInfos = indexInfoRepository.findAll();
        List<SyncJob> updateJobs = indexInfoSyncJobService.createSyncJobsForExistingIndexes(indexInfosFromOpenAPI, existingIndexInfos);
        List<SyncJob> createJobs = indexInfoSyncJobService.createSyncJobsForNewIndexes(indexInfosFromOpenAPI, existingIndexInfos);

        // 모든 SyncJob 저장
        List<SyncJob> allJobs = new ArrayList<>(updateJobs);
        allJobs.addAll(createJobs);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(allJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        // 1. API 요청, 전체 다 요청
        List<StockDataResult> allIndexDataBetweenDates = indexDataSyncJobService.requestOpenAPIBetweenDate(indexDataSyncRequest);

        // 2. 이미 있는 데이터 확인
        List<SyncJob> existingIndexDataSyncJobs = indexDataSyncJobService.getExistingIndexSyncJob(indexDataSyncRequest);

        // 3. 이미 존재하는 데이터 제거
        List<StockDataResult> newStockIndexData = indexDataSyncJobService.filterExistingIndexData(allIndexDataBetweenDates, existingIndexDataSyncJobs);

        // 4. IndexData 저장 및 SyncJob 생성
        List<SyncJob> newIndexDataSyncJobs = indexDataSyncJobService.createSyncJobsForNewIndexData(newStockIndexData);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(newIndexDataSyncJobs);

        // 5. 갱신된 부분 반환
        savedSyncJobs.addAll(existingIndexDataSyncJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request) {
        Specification<SyncJob> filter = SyncJobSpecification.filter(
                request.jobType(),
                request.indexInfoId(),
                request.baseDateFrom(),
                request.baseDateTo(),
                request.worker(),
                request.jobTimeFrom(),
                request.jobTimeTo(),
                request.status()
        );


        Sort sort = Sort.by(
                Sort.Order.desc(jobTime.name())
        );
        if (request.sortDirection().equals(ASCENDING_SORT_DIRECTION) && request.sortField().equals(jobTime.name())) {
            sort = Sort.by(
                    Sort.Order.asc(jobTime.name())
            );
        }
        if (request.sortField().equals(targetDate.name())) {
            if (request.sortDirection().equals(ASCENDING_SORT_DIRECTION)) {
                sort = Sort.by(
                        Sort.Order.asc(targetDate.name()),
                        Sort.Order.asc("id")
                );
            }
            if (request.sortDirection().equals(DESCENDING_SORT_DIRECTION)) {
                sort = Sort.by(
                        Sort.Order.desc(targetDate.name()),
                        Sort.Order.desc("id")
                );
            }
        }


        if (request.cursor() != null && request.sortField().equals(jobTime.name())) {
            LocalDateTime cursor = LocalDateTime.parse(request.cursor());

            if (sort.getOrderFor(jobTime.name()).isAscending()) {
                filter = filter.and((root, query, cb) -> cb.greaterThan(root.get(request.sortField()), cursor));
            } else {
                filter = filter.and((root, query, cb) -> cb.lessThan(root.get(request.sortField()), cursor));
            }
        }
        if (request.cursor() != null && request.sortField().equals(SortField.targetDate.name())) {
            LocalDate cursor = LocalDate.parse(request.cursor());

            Sort finalSort = sort;
            filter = filter.and((root, query, cb) -> {
                if (finalSort.getOrderFor(targetDate.name()).isAscending()) {
                    Predicate greaterTargetDate = cb.greaterThan(root.get("targetDate"), cursor);
                    Predicate equalTargetDateAndGreaterId = cb.and(
                            cb.equal(root.get("targetDate"), cursor),
                            cb.greaterThan(root.get("id"), request.idAfter())
                    );
                    return cb.or(greaterTargetDate, equalTargetDateAndGreaterId);
                } else {
                    Predicate lessTargetDate = cb.lessThan(root.get("targetDate"), cursor);
                    Predicate equalTargetDateAndLessId = cb.and(
                            cb.equal(root.get("targetDate"), cursor),
                            cb.lessThan(root.get("id"), request.idAfter())
                    );
                    return cb.or(lessTargetDate, equalTargetDateAndLessId);
                }
            });
        }

        Pageable pageableWithDirection = PageRequest.of(
                0,
                request.size(),
                sort
        );

        Page<SyncJob> syncJobPage = syncJobRepository.findAll(filter, pageableWithDirection);
        return CursorPageResponseSyncJob.of(syncJobPage, request.sortField());
    }
}
