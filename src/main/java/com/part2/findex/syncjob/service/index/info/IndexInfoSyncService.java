package com.part2.findex.syncjob.service.index.info;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.service.common.DummyFactory;
import com.part2.findex.syncjob.service.common.EntityBatchFlusher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class IndexInfoSyncService {

    private final EntityBatchFlusher entityBatchFlusher;
    private final IndexInfoSyncJobService indexInfoSyncService;
    private final DummyFactory dummyFactory;

    public List<StockIndexInfoResult> getExistingIndexInfoResults(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, List<IndexInfo> existingAllIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(existingAllIndexInfos);
        return allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    return existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();
    }

    public List<SyncJob> updateExistingIndexInfosAndSaveSyncJobs(List<IndexInfo> allIndexInfo, List<StockIndexInfoResult> existingStockIndexInfoResults, List<SyncJob> existingIndexInfoSyncJobs) {
        List<StockIndexInfoResult> stockIndexInfoResultsToUpdate = getStockIndexInfoResultToUpdate(existingStockIndexInfoResults, existingIndexInfoSyncJobs);

        Map<IndexInfoBusinessKey, IndexInfo> allIndexInfos = allIndexInfo.stream()
                .collect(Collectors.toMap(IndexInfo::getIndexInfoBusinessKey, Function.identity()));
        return entityBatchFlusher.processBatch(stockIndexInfoResultsToUpdate, stockIndexInfoResultToUpdate -> {
            IndexInfo updatedIndexInfo = indexInfoSyncService.updateIndexInfo(allIndexInfos, stockIndexInfoResultToUpdate);
            return indexInfoSyncService.saveIndexInfoSyncJob(SyncJobType.INDEX_INFO, SyncJobStatus.SUCCESS, updatedIndexInfo, stockIndexInfoResultToUpdate.baseDateTime());
        });
    }

    public List<SyncJob> createNewIndexInfosAndSaveSyncJobs(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, List<IndexInfo> existingAllIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(existingAllIndexInfos);
        List<StockIndexInfoResult> newStockIndexInfoResults = allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    return !existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();

        return entityBatchFlusher.processBatch(newStockIndexInfoResults, this::saveIndexInfoSyncJob);
    }

    private List<StockIndexInfoResult> getStockIndexInfoResultToUpdate(List<StockIndexInfoResult> existingStockIndexInfoResults, List<SyncJob> existingIndexInfoSyncJobs) {
        Set<SyncJob> alreadyUpdatedSyncJobs = new HashSet<>(existingIndexInfoSyncJobs);
        return existingStockIndexInfoResults.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    SyncJob tempSyncJob = dummyFactory.createDummyIndexInfoSyncJob(stockIndexInfoResult, tempIndexInfo);
                    return !alreadyUpdatedSyncJobs.contains(tempSyncJob);
                })
                .toList();
    }

    private SyncJob saveIndexInfoSyncJob(StockIndexInfoResult stockIndexInfoResult) {
        try {
            IndexInfo savedIndexInfo = convertToNewIndexInfo(stockIndexInfoResult);
            return indexInfoSyncService.saveNewIndexInfoSyncJobAndSetAutoSync(SyncJobType.INDEX_INFO, SyncJobStatus.SUCCESS, savedIndexInfo, stockIndexInfoResult.baseDateTime());
        } catch (Exception e) {
            IndexInfo failedIndexInfo = convertToNewIndexInfo(stockIndexInfoResult);
            return indexInfoSyncService.saveIndexInfoSyncJob(SyncJobType.INDEX_INFO, SyncJobStatus.FAILED, failedIndexInfo, stockIndexInfoResult.baseDateTime());
        }
    }


    private IndexInfo convertToNewIndexInfo(StockIndexInfoResult stockIndexInfo) {
        return new IndexInfo(stockIndexInfo.indexClassification(), stockIndexInfo.indexName(), stockIndexInfo.employedItemsCount(), stockIndexInfo.basePointInTime(), stockIndexInfo.baseIndex(), false, SourceType.OPEN_API);
    }
}