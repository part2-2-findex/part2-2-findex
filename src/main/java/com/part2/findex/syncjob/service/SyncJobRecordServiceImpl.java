package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.indexinfo.entity.IndexInfoBussinessKey;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyncJobRecordServiceImpl implements SyncJobRecordService {
    private final OpenApiStockIndexService openApiStockIndexService;
    private final SyncJobService syncJobService;
    private final IndexInfoRepository indexinfoRepository;
    private final SyncJobRepository syncJobRepository;

    @Transactional
    @Override
    public List<SyncJobResult> synchronizeIndexInfo() {
        // API로 갱신
        Map<IndexInfoBussinessKey, StockIndexInfoResult> indexInfosFromOpenAPI = openApiStockIndexService.getAllIndexInfoFromOpenAPI();

        // DB에 있는 지수 업데이트, N+1 발생가능
        List<IndexInfo> existingIndexInfos = indexinfoRepository.findAll();
        List<SyncJob> syncJobs = syncJobService.processBatchTemplate(existingIndexInfos,
                syncJobService.createUpdateFunction(indexInfosFromOpenAPI));

        // DB에 없던 완전 새로운 지수 정보 추출 후 저장
        List<StockIndexInfoResult> onlyNewStockIndexInfos = getOnlyNewStockIndexInfos(indexInfosFromOpenAPI, existingIndexInfos);
        List<SyncJob> syncJobs1 = syncJobService.processBatchTemplate(onlyNewStockIndexInfos,
                syncJobService.createSaveFunction());

        // 히스토리 통합
        syncJobs.addAll(syncJobs1);
        // 여기서 한번 더 넣어주는 걸로 하자
        syncJobService.processBatchTemplateOrigin(syncJobs, syncJobService.createSyncJodSaveFunction());

        return syncJobs.stream()
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

    private List<StockIndexInfoResult> getOnlyNewStockIndexInfos(
            Map<IndexInfoBussinessKey, StockIndexInfoResult> indexInfosFromOpenAPI, List<IndexInfo> existingIndexInfos
    ) {
        Set<IndexInfoBussinessKey> existingKeys = existingIndexInfos.stream()
                .map(IndexInfo::getIndexInfoBussinessKey)
                .collect(Collectors.toSet());

        return indexInfosFromOpenAPI.entrySet()
                .stream()
                .filter(info -> !existingKeys.contains(info.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
