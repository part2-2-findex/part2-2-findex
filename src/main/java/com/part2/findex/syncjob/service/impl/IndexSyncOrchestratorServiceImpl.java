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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public List<SyncJobResult> synchronizeIndexInfo() {
        // 0. 전체 지수 데이터 요청(최신 Date 요청 필요)
        List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI = openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI();

        // 1. 전체 지수정보 로드
        List<IndexInfo> allIndexInfo = indexInfoRepository.findAll();
        // 2.  DB에 존재하는 지수정보 추출
        Set<IndexInfo> existingIndexInfos = new HashSet<>(allIndexInfo);
        List<StockIndexInfoResult> existingStockIndexInfoResults = indexInfoSyncJobService.getExistingStockIndexInfoResults(allLastDateIndexInfoFromOpenAPI, existingIndexInfos);
        // 2-1.  DB에 존재하고 이미 연동된 지수 정보 연동 기록 반환
        List<SyncJob> existingIndexInfoSyncJobs = indexInfoSyncJobService.getExistingIndexInfoSyncJobs(existingStockIndexInfoResults);

        // 2-2.  DB에 존재하지만 연동이 안된 지수 정보 연동하고 연동 기록 반환
        List<SyncJob> updatedIndexInfoSyncJobs = indexInfoSyncJobService.getExistingNotSyncIndexInfoSyncJobs(allIndexInfo, existingStockIndexInfoResults, existingIndexInfoSyncJobs);

        // 3. DB에 존재하지 않는 새로운 지수정보 레포에 저장
        List<SyncJob> newIndexInfoSyncJobs = indexInfoSyncJobService.getNotExistingIndexInfoSyncJobs(allLastDateIndexInfoFromOpenAPI, existingIndexInfos);

        // 4. SyncJobResult 반환
        updatedIndexInfoSyncJobs.addAll(newIndexInfoSyncJobs);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(updatedIndexInfoSyncJobs);
        savedSyncJobs.addAll(existingIndexInfoSyncJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        // 1. API 요청, 전체 다 요청
        List<StockDataResult> allIndexDataBetweenDates = indexDataSyncJobService.requestOpenAPIBetweenDate(indexDataSyncRequest);

        // 2. DB에 이미 있는 데이터 확인
        List<SyncJob> existingIndexDataSyncJobs = indexDataSyncJobService.getExistingIndexSyncJob(indexDataSyncRequest);

        // 3. DB에 없는 새로운 데이터 추출
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
