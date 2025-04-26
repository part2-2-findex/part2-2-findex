package com.part2.findex.syncjob.service.orchestarorimpl;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.constant.SyncJobType;
import com.part2.findex.syncjob.dto.*;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.repository.SyncJobSpecification;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import com.part2.findex.syncjob.service.impl.IndexDataSyncJobService;
import com.part2.findex.syncjob.service.impl.IndexInfoSyncJobService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.part2.findex.syncjob.constant.SortDirectionConstant.ASCENDING_SORT_DIRECTION;

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
        // 이미 있는지 확인
        Map<Long, List<SyncJob>> existingIndexDataSyncJob = getExistingIndexSyncJob(indexDataSyncRequest);
        Map<Long, List<LocalDate>> missingIndexData = indexDataSyncJobService.findMissingIndexDates(indexDataSyncRequest, existingIndexDataSyncJob);

        // API 요청
        List<IndexInfo> allIndexInfoById = indexDataSyncJobService.fetchIndexInfosForMissingDates(missingIndexData);
        List<StockDataResult> allIndexDataBetweenDates = indexDataSyncJobService
                .createOpenAPIRequestsFromMissingDates(missingIndexData, allIndexInfoById);

        // IndexData 저장 및 SyncJob 생성
        List<SyncJob> syncJobsForExistingIndexes = indexDataSyncJobService.createSyncJobsForNewIndexData(allIndexInfoById, allIndexDataBetweenDates);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(syncJobsForExistingIndexes);

        // SyncJob 저장
        List<SyncJob> newSyncJobs = existingIndexDataSyncJob.values()
                .stream()
                .flatMap(List::stream)
                .toList();
        savedSyncJobs.addAll(newSyncJobs);

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

        Sort.Direction pageSortDirection = Sort.Direction.DESC;
        if (request.sortDirection().equals(ASCENDING_SORT_DIRECTION)) {
            pageSortDirection = Sort.Direction.ASC;
        }
        Pageable pageableWithDirection = PageRequest.of(
                0,
                request.size(),
                pageSortDirection,
                request.sortField()
        );
        log.warn(pageableWithDirection.toString());

        Page<SyncJob> syncJobPage = syncJobRepository.findAll(filter, pageableWithDirection);
        return CursorPageResponseSyncJob.of(syncJobPage, request.sortField());
    }

    private Map<Long, List<SyncJob>> getExistingIndexSyncJob(IndexDataSyncRequest indexDataSyncRequest) {
        List<SyncJob> existingIndexInfoIn = syncJobRepository
                .findByTargetDateBetweenAndIndexInfoIdInAndJobType(
                        indexDataSyncRequest.baseDateFrom(),
                        indexDataSyncRequest.baseDateTo(),
                        indexDataSyncRequest.indexInfoIds(),
                        SyncJobType.INDEX_DATA);

        return existingIndexInfoIn
                .stream()
                .collect(Collectors.groupingBy(
                        syncJob -> syncJob.getIndexInfo().getId()
                ));
    }
}
