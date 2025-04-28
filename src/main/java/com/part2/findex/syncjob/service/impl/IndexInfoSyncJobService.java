package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobKey;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
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

@RequiredArgsConstructor
@Service
public class IndexInfoSyncJobService {
    private final IndexSyncBatchService indexSyncBatchService;
    private final IndexInfoSyncService indexInfoSyncService;
    private final ClientIpResolver clientIpResolver;
    private final SyncJobRepository syncJobRepository;
    private final DummyFactory dummyFactory;

    public List<StockIndexInfoResult> filterExistingIndexInfoResults(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, Set<IndexInfo> existingIndexInfos) {
        return allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    return existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();
    }

    public List<SyncJob> getExistingIndexInfoSyncJobs(List<StockIndexInfoResult> existingStockIndexInfoResults) {
        List<SyncJobKey> syncJobKey = existingStockIndexInfoResults.stream()
                .map(this::createSyncJobKey)
                .toList();

        return syncJobRepository.findByKeys(syncJobKey);
    }

    public List<SyncJob> updateExistingIndexInfosAndCreateSyncJobs(List<IndexInfo> allIndexInfo,
                                                                   List<StockIndexInfoResult> existingStockIndexInfoResults,
                                                                   List<SyncJob> existingIndexInfoSyncJobs) {
        Set<SyncJob> existingSyncJobs = new HashSet<>(existingIndexInfoSyncJobs);
        List<StockIndexInfoResult> existingNotSyncStockIndexInfoResults = existingStockIndexInfoResults.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);
                    SyncJob tempSyncJob = dummyFactory.createDummyIndexInfoSyncJob(stockIndexInfoResult, tempIndexInfo);
                    return !existingSyncJobs.contains(tempSyncJob);
                })
                .toList();

        return this.updateIndexInfoAndCreateSyncJobs(existingNotSyncStockIndexInfoResults, allIndexInfo);
    }

    public List<SyncJob> createIndexInfosAndCreateSyncJobs(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, Set<IndexInfo> existingIndexInfos) {
        List<StockIndexInfoResult> newStockIndexInfoResults = allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);

                    return !existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();

        return this.saveIndexInfoAndCreateSyncJobs(newStockIndexInfoResults);
    }


    private SyncJobKey createSyncJobKey(StockIndexInfoResult stockIndexInfoResult) {
        return new SyncJobKey(
                SyncJobType.INDEX_INFO,
                LocalDate.parse(stockIndexInfoResult.baseDateTime(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                dummyFactory.createDummyIndexInfo(stockIndexInfoResult)
        );
    }

    private List<SyncJob> updateIndexInfoAndCreateSyncJobs(
            List<StockIndexInfoResult> existingNotSyncStockIndexInfoResults,
            List<IndexInfo> allIndexInfo
    ) {
        Map<IndexInfoBusinessKey, IndexInfo> existingIndexInfos = allIndexInfo.stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexInfoBusinessKey,
                        Function.identity()
                ));

        Function<StockIndexInfoResult, IndexInfo> updateFunction = existingNotSyncStockIndexInfoResult -> {
            IndexInfoBusinessKey infoBusinessKey = new IndexInfoBusinessKey(existingNotSyncStockIndexInfoResult.indexClassification(), existingNotSyncStockIndexInfoResult.indexName());
            IndexInfo indexInfoSameKey = existingIndexInfos.get(infoBusinessKey);

            return indexInfoSyncService.updateIndexInfo(indexInfoSameKey, existingNotSyncStockIndexInfoResult);
        };

        return this.processWithIndexInfoSyncJobs(existingNotSyncStockIndexInfoResults, updateFunction);
    }

    private List<SyncJob> saveIndexInfoAndCreateSyncJobs(
            List<StockIndexInfoResult> newStockIndexInfoResults
    ) {
        Function<StockIndexInfoResult, IndexInfo> saveFunction = indexInfoSyncService::saveNewIndexInfo;

        return this.processWithIndexInfoSyncJobs(newStockIndexInfoResults, saveFunction);
    }

    private List<SyncJob> processWithIndexInfoSyncJobs(List<StockIndexInfoResult> items, Function<StockIndexInfoResult, IndexInfo> processor) {
        return indexSyncBatchService.processBatch(items, item -> {
            try {
                IndexInfo indexInfo = processor.apply(item);
                return createSyncJob(SyncJobType.INDEX_INFO, indexInfo, SyncJobStatus.SUCCESS, item.baseDateTime());
            } catch (Exception e) {
                IndexInfo failedIndexInfo = indexInfoSyncService.convertToIndexInfo(item);
                syncJobRepository.save(createSyncJob(SyncJobType.INDEX_INFO, failedIndexInfo, SyncJobStatus.FAILED, item.baseDateTime()));
                throw new IllegalArgumentException("지수정보 연동 실패");
            }
        });
    }


    private SyncJob createSyncJob(SyncJobType jobType, IndexInfo indexInfo, SyncJobStatus status, String stockIndexBaseDate) {
        String clientIp = clientIpResolver.getClientIp();
        LocalDate baseDate = getLocalDate(stockIndexBaseDate);

        return new SyncJob(
                jobType,
                baseDate,
                clientIp,
                LocalDateTime.now(),
                status,
                indexInfo
        );
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