package com.part2.findex.syncjob.service.impl;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.autosync.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
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
    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final ClientIpResolver clientIpResolver;
    private final IndexSyncBatchService indexSyncBatchService;
    private final IndexInfoRepository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final SyncJobRepository syncJobRepository;
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

        Map<IndexInfoBusinessKey, IndexInfo> existingIndexInfos = allIndexInfo.stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexInfoBusinessKey,
                        Function.identity()));
        Function<StockIndexInfoResult, IndexInfo> updateFunction = existingNotSyncStockIndexInfoResult -> {
            IndexInfoBusinessKey infoBusinessKey = new IndexInfoBusinessKey(existingNotSyncStockIndexInfoResult.indexClassification(), existingNotSyncStockIndexInfoResult.indexName());
            IndexInfo existingIndexInfo = existingIndexInfos.get(infoBusinessKey);
            existingIndexInfo.update(existingNotSyncStockIndexInfoResult.employedItemsCount(), existingNotSyncStockIndexInfoResult.basePointInTime(), existingNotSyncStockIndexInfoResult.baseIndex(), null, BASE_INDEX_TOLERANCE);

            return existingIndexInfo;
        };

        return this.processWithIndexInfoSyncJobs(existingNotSyncStockIndexInfoResults, updateFunction);
    }

    public List<SyncJob> createIndexInfosAndCreateSyncJobs(List<StockIndexInfoResult> allLastDateIndexInfoFromOpenAPI, List<IndexInfo> existingAllIndexInfos) {
        Set<IndexInfo> existingIndexInfos = new HashSet<>(existingAllIndexInfos);

        List<StockIndexInfoResult> newStockIndexInfoResults = allLastDateIndexInfoFromOpenAPI.stream()
                .filter(stockIndexInfoResult -> {
                    IndexInfo tempIndexInfo = dummyFactory.createDummyIndexInfo(stockIndexInfoResult);

                    return !existingIndexInfos.contains(tempIndexInfo);
                })
                .toList();

        Function<StockIndexInfoResult, IndexInfo> saveFunction = this::saveNewIndexInfo;
        return this.processWithIndexInfoSyncJobs(newStockIndexInfoResults, saveFunction);
    }

    public IndexInfo saveNewIndexInfo(StockIndexInfoResult newIndexInfo) {
        IndexInfo savedIndexInfo = indexInfoRepository.save(convertToNewIndexInfo(newIndexInfo));
        AutoSyncConfig config = AutoSyncConfig.builder()
                .indexInfo(savedIndexInfo)
                .enabled(false)
                .build();
        autoSyncConfigRepository.save(config);

        return savedIndexInfo;
    }


    private SyncJobKey createSyncJobKey(StockIndexInfoResult stockIndexInfoResult) {
        return new SyncJobKey(
                SyncJobType.INDEX_INFO,
                LocalDate.parse(stockIndexInfoResult.baseDateTime(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                dummyFactory.createDummyIndexInfo(stockIndexInfoResult)
        );
    }

    private List<SyncJob> processWithIndexInfoSyncJobs(List<StockIndexInfoResult> items, Function<StockIndexInfoResult, IndexInfo> processor) {
        return indexSyncBatchService.processBatch(items, item -> {
            try {
                IndexInfo indexInfo = processor.apply(item);
                return createSyncJob(SyncJobType.INDEX_INFO, indexInfo, SyncJobStatus.SUCCESS, item.baseDateTime());
            } catch (Exception e) {
                IndexInfo failedIndexInfo = convertToNewIndexInfo(item);
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

    private IndexInfo convertToNewIndexInfo(StockIndexInfoResult stockIndexInfo) {
        return new IndexInfo(
                stockIndexInfo.indexClassification(),
                stockIndexInfo.indexName(),
                stockIndexInfo.employedItemsCount(),
                stockIndexInfo.basePointInTime(),
                stockIndexInfo.baseIndex(),
                false,
                SourceType.OPEN_API);
    }
}