package com.part2.findex.syncjob.service.index;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.*;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobKey;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.repository.SyncJobSpecification;
import com.part2.findex.syncjob.service.common.DummyFactory;
import com.part2.findex.syncjob.service.common.TargetDateService;
import com.part2.findex.syncjob.service.index.data.IndexDataSyncService;
import com.part2.findex.syncjob.service.index.info.IndexInfoSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.part2.findex.syncjob.constant.SortDirectionConstant.ASCENDING_SORT_DIRECTION;
import static com.part2.findex.syncjob.constant.SortDirectionConstant.DESCENDING_SORT_DIRECTION;
import static com.part2.findex.syncjob.constant.SortField.jobTime;
import static com.part2.findex.syncjob.constant.SortField.targetDate;

@Service
@RequiredArgsConstructor
public class IndexSyncServiceImpl implements IndexSyncService {
    private final OpenApiStockIndexService openApiStockIndexService;
    private final IndexInfoSyncService indexInfoSyncJobService;
    private final IndexDataSyncService indexDataSyncService;
    private final TargetDateService targetDateService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final DummyFactory dummyFactory;

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexInfo() {
        List<StockIndexInfoResult> allStockInfoInLastDate = openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI(targetDateService.getLatestBusinessDay());

        List<IndexInfo> existingAllIndexInfos = indexInfoRepository.findAll();
        List<StockIndexInfoResult> existingStockInfoResults = indexInfoSyncJobService.getExistingIndexInfoResults(allStockInfoInLastDate, existingAllIndexInfos);

        List<SyncJob> completedIndexSyncJobs = getCompletedIndexInfoSyncJobs(existingStockInfoResults);
        List<SyncJob> updatedIndexInfoSyncJobs = indexInfoSyncJobService.updateExistingIndexInfosAndSaveSyncJobs(existingAllIndexInfos, existingStockInfoResults, completedIndexSyncJobs);
        List<SyncJob> newIndexInfoSyncJobs = indexInfoSyncJobService.createNewIndexInfosAndSaveSyncJobs(allStockInfoInLastDate, existingAllIndexInfos);

        completedIndexSyncJobs.addAll(updatedIndexInfoSyncJobs);
        completedIndexSyncJobs.addAll(newIndexInfoSyncJobs);
        return completedIndexSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        List<IndexInfo> requestedIndexInfos = indexInfoRepository.findAllById(indexDataSyncRequest.indexInfoIds());
        List<StockDataResult> allStockDataBetweenDates = requestOpenAPIBetweenDate(indexDataSyncRequest, requestedIndexInfos);

        List<SyncJob> completedIndexDataSyncJobs = syncJobRepository.findByTargetDateBetweenAndIndexInfoIdInAndJobType(indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo(), indexDataSyncRequest.indexInfoIds(), SyncJobType.INDEX_DATA);
        List<StockDataResult> newStockDataResults = indexDataSyncService.filterExistingStockData(allStockDataBetweenDates, completedIndexDataSyncJobs);
        List<SyncJob> newIndexDataSyncJobs = indexDataSyncService.createSyncJobsForNewIndexData(newStockDataResults, requestedIndexInfos);

        completedIndexDataSyncJobs.addAll(newIndexDataSyncJobs);
        return completedIndexDataSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request) {
        Specification<SyncJob> baseFilter = SyncJobSpecification.filter(request.jobType(), request.indexInfoId(), request.baseDateFrom(), request.baseDateTo(), request.worker(), request.jobTimeFrom(), request.jobTimeTo(), request.status());
        Sort sort = getSortOrders(request);
        Specification<SyncJob> cusurSpecification = SyncJobSpecification.getSyncJobSpecification(request, baseFilter, sort);

        Pageable pageableWithDirection = PageRequest.of(0, request.size(), sort);
        Page<SyncJob> syncJobPage = syncJobRepository.findAll(cusurSpecification, pageableWithDirection);

        Page<SyncJob> totalPage = syncJobRepository.findAll(baseFilter, pageableWithDirection);
        return CursorPageResponseSyncJob.of(syncJobPage, request.sortField(), totalPage.getTotalElements());
    }

    private Sort getSortOrders(SyncJobQueryRequest request) {
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
        return sort;
    }

    private List<StockDataResult> requestOpenAPIBetweenDate(IndexDataSyncRequest indexDataSyncRequest, List<IndexInfo> requestedIndexInfos) {
        List<IndexDataOpenAPIRequest> openAPIRequests = requestedIndexInfos.stream()
                .map(indexInfo -> IndexDataOpenAPIRequest.of(indexInfo, indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo()))
                .toList();

        Set<IndexInfo> existingIndexInfos = new HashSet<>(requestedIndexInfos);
        return openApiStockIndexService.getAllIndexDataBetweenDates(openAPIRequests)
                .stream()
                .filter(stockDataResult -> {
                    IndexInfo dummyIndexInfo = dummyFactory.createDummyIndexInfoFromStockData(stockDataResult);
                    return existingIndexInfos.contains(dummyIndexInfo);
                })
                .toList();
    }

    private List<SyncJob> getCompletedIndexInfoSyncJobs(List<StockIndexInfoResult> existingStockIndexInfoResults) {
        List<SyncJobKey> syncJobKey = existingStockIndexInfoResults.stream()
                .map(this::createIndexInfoSyncJobKey)
                .toList();
        return syncJobRepository.findByKeys(syncJobKey);
    }

    private SyncJobKey createIndexInfoSyncJobKey(StockIndexInfoResult stockIndexInfoResult) {
        return new SyncJobKey(SyncJobType.INDEX_INFO, LocalDate.parse(stockIndexInfoResult.baseDateTime(), DateTimeFormatter.ofPattern("yyyyMMdd")), dummyFactory.createDummyIndexInfo(stockIndexInfoResult));
    }
}
