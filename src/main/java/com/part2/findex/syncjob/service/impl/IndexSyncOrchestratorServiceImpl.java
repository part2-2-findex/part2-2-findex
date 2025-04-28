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
    private final IndexDateService indexDateService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexInfo() {
        LocalDate targetDate = indexDateService.getLatestBusinessDay();
        List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI = openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI(targetDate);

        List<IndexInfo> allIndexInfo = indexInfoRepository.findAll();
        Set<IndexInfo> existingIndexInfos = new HashSet<>(allIndexInfo);
        List<StockIndexInfoResult> existingStockIndexInfoResults = indexInfoSyncJobService.getExistingStockIndexInfoResults(allLastDateIndexInfoFromOpenAPI, existingIndexInfos);
        List<SyncJob> existingIndexInfoSyncJobs = indexInfoSyncJobService.getExistingIndexInfoSyncJobs(existingStockIndexInfoResults);

        List<SyncJob> updatedIndexInfoSyncJobs = indexInfoSyncJobService.getExistingNotSyncIndexInfoSyncJobs(allIndexInfo, existingStockIndexInfoResults, existingIndexInfoSyncJobs);
        List<SyncJob> newIndexInfoSyncJobs = indexInfoSyncJobService.getNotExistingIndexInfoSyncJobs(allLastDateIndexInfoFromOpenAPI, existingIndexInfos);

        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(newIndexInfoSyncJobs);
        savedSyncJobs.addAll(existingIndexInfoSyncJobs);
        savedSyncJobs.addAll(updatedIndexInfoSyncJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        List<StockDataResult> allIndexDataBetweenDates = indexDataSyncJobService.requestOpenAPIBetweenDate(indexDataSyncRequest);

        List<SyncJob> existingIndexDataSyncJobs = indexDataSyncJobService.getExistingIndexSyncJob(indexDataSyncRequest);

        List<StockDataResult> newStockIndexData = indexDataSyncJobService.filterExistingIndexData(allIndexDataBetweenDates, existingIndexDataSyncJobs);

        List<SyncJob> newIndexDataSyncJobs = indexDataSyncJobService.createSyncJobsForNewIndexData(newStockIndexData);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(newIndexDataSyncJobs);

        savedSyncJobs.addAll(existingIndexDataSyncJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request) {
        Specification<SyncJob> baseFilter = SyncJobSpecification.filter(
                request.jobType(),
                request.indexInfoId(),
                request.baseDateFrom(),
                request.baseDateTo(),
                request.worker(),
                request.jobTimeFrom(),
                request.jobTimeTo(),
                request.status()
        );


        Sort sort = getSortOrders(request);
        Specification<SyncJob> cusurSpecification = getSyncJobSpecification(request, baseFilter, sort);

        Pageable pageableWithDirection = PageRequest.of(0, request.size(), sort);
        Page<SyncJob> syncJobPage = syncJobRepository.findAll(cusurSpecification, pageableWithDirection);
        Page<SyncJob> totalPage = syncJobRepository.findAll(baseFilter, pageableWithDirection);

        return CursorPageResponseSyncJob.of(syncJobPage, request.sortField(), totalPage.getTotalElements());
    }

    private Specification<SyncJob> getSyncJobSpecification(SyncJobQueryRequest request, Specification<SyncJob> baseFilter, Sort sort) {
        Specification<SyncJob> finalFilter = Specification.where(baseFilter);

        if (request.cursor() != null && request.sortField().equals(jobTime.name())) {
            LocalDateTime cursor = LocalDateTime.parse(request.cursor());

            if (sort.getOrderFor(jobTime.name()).isAscending()) {
                finalFilter = finalFilter.and((root, query, cb) -> cb.greaterThan(root.get(request.sortField()), cursor));
            } else {
                finalFilter = finalFilter.and((root, query, cb) -> cb.lessThan(root.get(request.sortField()), cursor));
            }
        }
        if (request.cursor() != null && request.sortField().equals(SortField.targetDate.name())) {
            LocalDate cursor = LocalDate.parse(request.cursor());

            finalFilter = finalFilter.and((root, query, cb) -> {
                if (sort.getOrderFor(targetDate.name()).isAscending()) {
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

        return finalFilter;
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
}
