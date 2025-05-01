package com.part2.findex.syncjob.service.index.info;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobBusinessKey;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.service.common.DummyFactory;
import com.part2.findex.syncjob.service.common.EntityBatchFlusher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class IndexInfoSyncService {

    private final IndexInfoSyncJobService indexInfoSyncJobService;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final EntityBatchFlusher entityBatchFlusher;
    private final DummyFactory dummyFactory;

    public List<SyncJob> getIndexInfoSyncJobs(List<IndexInfo> existingAllIndexInfos, List<StockIndexInfoResult> allStockInfoInLastDate) {
        List<StockIndexInfoResult> existingStockInfoResults = getExistingIndexInfoResults(allStockInfoInLastDate, existingAllIndexInfos);
        List<SyncJob> completedIndexSyncJobs = getCompletedIndexInfoSyncJobs(existingStockInfoResults);
        List<SyncJob> updatedIndexInfoSyncJobs = updateExistingIndexInfosAndSaveSyncJobs(existingAllIndexInfos, existingStockInfoResults, completedIndexSyncJobs);
        List<SyncJob> newIndexInfoSyncJobs = createNewIndexInfosAndSaveSyncJobs(allStockInfoInLastDate, existingAllIndexInfos);
        completedIndexSyncJobs.addAll(updatedIndexInfoSyncJobs);
        completedIndexSyncJobs.addAll(newIndexInfoSyncJobs);

        return completedIndexSyncJobs;
    }

    private List<StockIndexInfoResult> getExistingIndexInfoResults(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, List<IndexInfo> existingAllIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(existingAllIndexInfos);
        return allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    return existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();
    }

    private List<SyncJob> updateExistingIndexInfosAndSaveSyncJobs(List<IndexInfo> allIndexInfo, List<StockIndexInfoResult> existingStockIndexInfoResults, List<SyncJob> existingIndexInfoSyncJobs) {
        List<StockIndexInfoResult> stockIndexInfoResultsToUpdate = getStockIndexInfoResultToUpdate(existingStockIndexInfoResults, existingIndexInfoSyncJobs);
        Map<IndexInfoBusinessKey, IndexInfo> allIndexInfos = allIndexInfo.stream()
                .collect(Collectors.toMap(IndexInfo::getIndexInfoBusinessKey, Function.identity()));

        return entityBatchFlusher.processBatch(stockIndexInfoResultsToUpdate, stockIndexInfoResultToUpdate -> {
            IndexInfo updatedIndexInfo = indexInfoSyncJobService.updateIndexInfo(allIndexInfos, stockIndexInfoResultToUpdate);
            return indexInfoSyncJobService.saveIndexInfoSyncJob(SyncJobType.INDEX_INFO, SyncJobStatus.SUCCESS, updatedIndexInfo, stockIndexInfoResultToUpdate.baseDateTime());
        });
    }

    private List<SyncJob> createNewIndexInfosAndSaveSyncJobs(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, List<IndexInfo> existingAllIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(existingAllIndexInfos);
        List<StockIndexInfoResult> newStockIndexInfoResults = allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    return !existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();

        return entityBatchFlusher.processBatch(newStockIndexInfoResults, this::saveNewIndexInfoSyncJob);
    }

    private SyncJob saveNewIndexInfoSyncJob(StockIndexInfoResult stockIndexInfoResult) {
        try {
            IndexInfo savedIndexInfo = indexInfoRepository.save(convertToNewIndexInfo(stockIndexInfoResult));
            return indexInfoSyncJobService.saveIndexInfoSyncJobAndAutoSync(SyncJobType.INDEX_INFO, savedIndexInfo, SyncJobStatus.SUCCESS, stockIndexInfoResult.baseDateTime());
        } catch (Exception e) {
            throw new IllegalStateException("지수 정보 연동에 실패 했습니다.");
        }
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

    private IndexInfo convertToNewIndexInfo(StockIndexInfoResult stockIndexInfo) {
        return new IndexInfo(stockIndexInfo.indexClassification(), stockIndexInfo.indexName(), stockIndexInfo.employedItemsCount(), stockIndexInfo.basePointInTime(), stockIndexInfo.baseIndex(), false, SourceType.OPEN_API);
    }


    private List<SyncJob> getCompletedIndexInfoSyncJobs(List<StockIndexInfoResult> existingStockIndexInfoResults) {
        List<SyncJobBusinessKey> syncJobBusinessKey = existingStockIndexInfoResults.stream()
                .map(this::createIndexInfoSyncJobKey)
                .toList();

        return syncJobRepository.findByKeys(syncJobBusinessKey);
    }

    private SyncJobBusinessKey createIndexInfoSyncJobKey(StockIndexInfoResult stockIndexInfoResult) {
        return new SyncJobBusinessKey(SyncJobType.INDEX_INFO, LocalDate.parse(stockIndexInfoResult.baseDateTime(), DateTimeFormatter.ofPattern("yyyyMMdd")), dummyFactory.createDummyIndexInfo(stockIndexInfoResult));
    }
}