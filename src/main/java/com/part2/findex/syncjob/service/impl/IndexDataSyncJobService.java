package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.mapper.IndexInfoMapper;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexDataSyncJobService {
    private final ClientIpResolver clientIpResolver;
    private final IndexSyncBatchService indexSyncBatchService;
    private final IndexDataSyncService indexDataSyncService;
    private final SyncJobRepository syncJobRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final DummyFactory dummyFactory;

    public List<SyncJob> getExistingIndexDataSyncJob(IndexDataSyncRequest indexDataSyncRequest) {
        return syncJobRepository.findByTargetDateBetweenAndIndexInfoIdInAndJobType(
                indexDataSyncRequest.baseDateFrom(),
                indexDataSyncRequest.baseDateTo(),
                indexDataSyncRequest.indexInfoIds(),
                SyncJobType.INDEX_DATA);
    }

    public List<StockDataResult> filterExistingIndexData(List<StockDataResult> allIndexDataBetweenDates, List<SyncJob> existingIndexDataSyncJobs) {
        Set<SyncJob> existingSyncJobs = new HashSet<>(existingIndexDataSyncJobs);
        return allIndexDataBetweenDates.stream()
                .filter(stockDataResult -> {
                    IndexInfo indexInfoSignature = dummyFactory.createDummyIndexInfoFromStockData(stockDataResult);
                    SyncJob stockResultSyncJob = dummyFactory.createDummyIndexDataSyncJob(stockDataResult, indexInfoSignature);

                    return !existingSyncJobs.contains(stockResultSyncJob);
                })
                .toList();
    }

    public List<SyncJob> createSyncJobsForNewIndexData(List<StockDataResult> newStockDataResults) {
        List<IndexInfoBusinessKey> newStockDataIndexInfoBusinessKey = newStockDataResults.stream()
                .map(stockDataResult -> new IndexInfoBusinessKey(stockDataResult.indexClassification(), stockDataResult.indexName()))
                .toList();

        Map<IndexInfoBusinessKey, IndexInfo> infoBusinessKeyIndexInfoMap = indexInfoRepository.findByIndexInfoBusinessKeys(newStockDataIndexInfoBusinessKey)
                .stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexInfoBusinessKey,
                        Function.identity()
                ));

        return indexSyncBatchService.processBatch(newStockDataResults, stockDataResult -> {
            IndexInfo indexInfo = infoBusinessKeyIndexInfoMap.get(IndexInfoMapper.toIndexInfoBusinessKey(stockDataResult));
            return getSyncJob(stockDataResult, indexInfo);
        });
    }

    private SyncJob getSyncJob(StockDataResult stockDataResult, IndexInfo indexInfo) {
        try {
            IndexData indexData = indexDataSyncService.saveNewIndexData(stockDataResult, indexInfo);
            return createSyncJobIndexData(SyncJobType.INDEX_DATA, indexData, SyncJobStatus.SUCCESS);
        } catch (Exception e) {
            IndexData failedIndexData = indexDataSyncService.convertToIndexData(stockDataResult, indexInfo);
            indexDataSyncService.saveFailedIndexDataSyncJob(createSyncJobIndexData(SyncJobType.INDEX_DATA, failedIndexData, SyncJobStatus.FAILED));
            e.printStackTrace();
            throw new IllegalArgumentException("지수 데이터 연동 실패");
        }
    }

    private SyncJob createSyncJobIndexData(SyncJobType jobType, IndexData indexData, SyncJobStatus status) {
        String clientIp = clientIpResolver.getClientIp();
        LocalDate baseDate = getLocalDate(indexData.getBaseDate().toString());

        return new SyncJob(jobType, baseDate, clientIp, LocalDateTime.now(), status, indexData.getIndexInfo());
    }

    private LocalDate getLocalDate(String basePointInTime) {
        LocalDate baseDate;
        if (basePointInTime.contains("-")) {
            baseDate = LocalDate.parse(basePointInTime);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            baseDate = LocalDate.parse(basePointInTime, formatter);
        }

        return baseDate;
    }
}
