package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndexSyncOrchestratorServiceImpl implements IndexSyncOrchestratorService {
    private final OpenApiStockIndexService openApiStockIndexService;
    private final IndexSyncJobService indexSyncJobService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    @Override
    public List<SyncJobResult> syncIndexInfoWithOpenAPI() {
        Map<String, StockIndexInfoResult> indexInfosFromOpenAPI =
                openApiStockIndexService.getAllIndexInfoFromOpenAPI();

        // 지수정보 수정 및 등록, N+1문제 확인 필요
        List<IndexInfo> existingIndexInfos = indexInfoRepository.findAll();
        List<SyncJob> updateJobs = indexSyncJobService.createSyncJobsForExistingIndexes(indexInfosFromOpenAPI, existingIndexInfos);
        List<SyncJob> createJobs = indexSyncJobService.createSyncJobsForNewIndexes(indexInfosFromOpenAPI, existingIndexInfos);

        // 모든 SyncJob 저장
        List<SyncJob> allJobs = new ArrayList<>(updateJobs);
        allJobs.addAll(createJobs);
        List<SyncJob> savedSyncJobs = syncJobRepository.saveAllAndFlush(allJobs);

        return savedSyncJobs.stream()
                .map(SyncJobResult::from)
                .toList();
    }

    @Override
    public List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest) {
        return null;
    }


    @Override
    public List<SyncJobResult> getIndexSyncHistories() {
        return null;
    }
}
