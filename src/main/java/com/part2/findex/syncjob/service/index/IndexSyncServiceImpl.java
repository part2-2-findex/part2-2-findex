package com.part2.findex.syncjob.service.index;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.*;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobSortStrategy;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.repository.SyncJobSpecification;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexSyncServiceImpl implements IndexSyncService {

    private final OpenApiStockIndexService openApiStockIndexService;
    private final IndexInfoSyncService indexInfoSyncService;
    private final IndexDataSyncService indexDataSyncService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexInfo() {
        List<IndexInfo> existingAllIndexInfos = indexInfoRepository.findAll();
        List<StockIndexInfoResult> requestedIndexInfosFromOpenAPI = openApiStockIndexService.loadAllLastDateIndexInfoFromOpenAPI();

        return indexInfoSyncService.syncIndexInfosAndCreateJobs(existingAllIndexInfos, requestedIndexInfosFromOpenAPI)
                .stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        List<IndexInfo> requestedIndexInfos = indexInfoRepository.findAllById(indexDataSyncRequest.indexInfoIds());
        List<StockDataResult> requestedIndexDataFromOpenAPI = openApiStockIndexService.loadIndexDataFromOpenAPI(indexDataSyncRequest, requestedIndexInfos);

        return indexDataSyncService.syncIndexDataAndCreateJobs(indexDataSyncRequest, requestedIndexInfos, requestedIndexDataFromOpenAPI)
                .stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request) {
        Specification<SyncJob> baseFilter = SyncJobSpecification.filter(request.jobType(), request.indexInfoId(), request.baseDateFrom(), request.baseDateTo(), request.worker(), request.jobTimeFrom(), request.jobTimeTo(), request.status());
        Sort sort = SyncJobSortStrategy.fromField(request.sortField());
        Specification<SyncJob> cusurSpecification = SyncJobSpecification.getSyncJobSpecification(request, baseFilter, sort);

        Pageable pageableWithDirection = PageRequest.of(0, request.size(), sort);
        Page<SyncJob> syncJobPage = syncJobRepository.findAll(cusurSpecification, pageableWithDirection);
        Page<SyncJob> totalPage = syncJobRepository.findAll(baseFilter, pageableWithDirection);

        return CursorPageResponseSyncJob.of(syncJobPage, request.sortField(), totalPage.getTotalElements());
    }

}
