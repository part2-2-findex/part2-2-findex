package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<String, StockIndexInfoResult> indexInfosFromOpenAPI =
                openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI();

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

    @Override
    public List<SyncJobResult> getIndexSyncHistories() {
        return null;
    }
}
