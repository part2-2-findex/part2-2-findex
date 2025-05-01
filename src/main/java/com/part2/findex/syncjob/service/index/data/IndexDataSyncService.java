package com.part2.findex.syncjob.service.index.data;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.mapper.IndexInfoMapper;
import com.part2.findex.syncjob.repository.SyncJobRepository;
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
public class IndexDataSyncService {

    private final IndexDataSyncJobService indexDataSyncJobService;
    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;
    private final EntityBatchFlusher entityBatchFlusher;
    private final DummyFactory dummyFactory;

    public List<SyncJob> syncIndexDataAndCreateJobs(IndexDataSyncRequest indexDataSyncRequest, List<IndexInfo> requestedIndexInfos, List<StockDataResult> requestedIndexData) {
        List<SyncJob> completedIndexDataSyncJobs = syncJobRepository.findByTargetDateBetweenAndIndexInfoIdInAndJobType(indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo(), indexDataSyncRequest.indexInfoIds(), SyncJobType.INDEX_DATA);
        List<StockDataResult> stockDataForRequestedIndices = filterSameNameRequestedIndexData(requestedIndexData, requestedIndexInfos);
        List<StockDataResult> newStockData = filterExistingStockData(stockDataForRequestedIndices, completedIndexDataSyncJobs);
        List<SyncJob> newIndexDataSyncJobs = createSyncJobsForNewIndexData(newStockData, requestedIndexInfos);
        completedIndexDataSyncJobs.addAll(newIndexDataSyncJobs);

        return completedIndexDataSyncJobs;
    }

    private List<StockDataResult> filterSameNameRequestedIndexData(List<StockDataResult> allIndexDataBetweenDates, List<IndexInfo> requestedIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(requestedIndexInfos);
        return allIndexDataBetweenDates.stream()
                .filter(stockDataResult -> {
                    IndexInfo dummyIndexInfo = dummyFactory.createDummyIndexInfoFromStockData(stockDataResult);
                    return existingIndexInfos.contains(dummyIndexInfo);
                })
                .toList();
    }

    private List<StockDataResult> filterExistingStockData(List<StockDataResult> allIndexDataBetweenDates, List<SyncJob> existingIndexDataSyncJobs) {
        Set<SyncJob> existingSyncJobs = new HashSet<>(existingIndexDataSyncJobs);
        return allIndexDataBetweenDates.stream()
                .filter(stockDataResult -> {
                    IndexInfo indexInfoSignature = dummyFactory.createDummyIndexInfoFromStockData(stockDataResult);
                    SyncJob stockResultSyncJob = dummyFactory.createDummyIndexDataSyncJob(stockDataResult, indexInfoSignature);
                    return !existingSyncJobs.contains(stockResultSyncJob);
                })
                .toList();
    }

    private List<SyncJob> createSyncJobsForNewIndexData(List<StockDataResult> newStockDataResults, List<IndexInfo> requestedIndexInfos) {
        Map<IndexInfoBusinessKey, IndexInfo> existingIndexInfos = requestedIndexInfos.stream()
                .collect(Collectors.toMap(IndexInfo::getIndexInfoBusinessKey, Function.identity()));

        return entityBatchFlusher.processBatch(newStockDataResults, newStockDataResult -> {
            IndexInfo indexInfo = existingIndexInfos.get(IndexInfoMapper.toIndexInfoBusinessKey(newStockDataResult));
            return createSyncJobForIndexData(newStockDataResult, indexInfo);
        });
    }

    private SyncJob createSyncJobForIndexData(StockDataResult stockDataResult, IndexInfo indexInfo) {
        try {
            IndexData successIndexData = indexDataRepository.save(convertToIndexData(stockDataResult, indexInfo));
            return indexDataSyncJobService.saveIndexDataSyncJob(SyncJobType.INDEX_DATA, successIndexData, SyncJobStatus.SUCCESS);
        } catch (Exception e) {
            IndexData failedIndexData = convertToIndexData(stockDataResult, indexInfo);
            return indexDataSyncJobService.saveIndexDataSyncJob(SyncJobType.INDEX_DATA, failedIndexData, SyncJobStatus.FAILED);
        }
    }

    private IndexData convertToIndexData(StockDataResult stockDataResult, IndexInfo indexInfo) {
        return new IndexData(indexInfo, stockDataResult.baseDate(), SourceType.OPEN_API, stockDataResult.marketPrice(), stockDataResult.closingPrice(), stockDataResult.highPrice(), stockDataResult.lowPrice(), stockDataResult.versus(), stockDataResult.fluctuationRate(), stockDataResult.tradingQuantity(), stockDataResult.tradingPrice(), stockDataResult.marketTotalAmount());
    }
}
