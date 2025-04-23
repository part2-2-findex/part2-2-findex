package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IndexSyncJobService {
    private final IndexSyncBatchService indexSyncBatchService;
    private final IndexInfoSyncService indexInfoService;
    private final ClientIpResolver clientIpResolver;

    public List<SyncJob> createSyncJobsForNewIndexes(
            Map<String, StockIndexInfoResult> indexInfosFromOpenAPI,
            List<IndexInfo> existingIndexInfos
    ) {
        List<StockIndexInfoResult> onlyNewStockIndexInfos =
                filterNewIndexInfoResults(indexInfosFromOpenAPI, existingIndexInfos);

        Function<StockIndexInfoResult, IndexInfo> saveFunction = indexInfoService::saveNewIndexInfo;

        return this.processWithIndexInfoSyncJobs(
                onlyNewStockIndexInfos,
                saveFunction
        );
    }

    public List<SyncJob> createSyncJobsForExistingIndexes(
            Map<String, StockIndexInfoResult> indexInfosFromOpenAPI,
            List<IndexInfo> existingIndexInfos
    ) {
        Function<IndexInfo, IndexInfo> updateFunction = existingIndexInfo -> {
            StockIndexInfoResult updated = indexInfosFromOpenAPI.get(
                    existingIndexInfo.getIndexInfoBusinessKey().toString());
            return indexInfoService.updateIndexInfo(existingIndexInfo, updated);
        };

        return this.processWithIndexInfoSyncJobs(
                existingIndexInfos,
                updateFunction
        );
    }

    private <T> List<SyncJob> processWithIndexInfoSyncJobs(
            List<T> items,
            Function<T, IndexInfo> processor) {

        return indexSyncBatchService.processBatch(items, item -> {

            SyncJobStatus status;
            IndexInfo indexInfo = null;
            try {
                indexInfo = processor.apply(item);
                status = SyncJobStatus.SUCCESS;
            } catch (Exception e) {
                status = SyncJobStatus.FAILED;
            }

            assert indexInfo != null;
            return createSyncJob(SyncJobType.INDEX_INFO, indexInfo, status);
        });
    }

    private SyncJob createSyncJob(SyncJobType jobType,
                                  IndexInfo indexInfo,
                                  SyncJobStatus status) {
        String clientIp = clientIpResolver.getClientIp();
        String basePointInTime = indexInfo.getBasePointInTime();
        LocalDate baseDate;
        if (basePointInTime.contains("-")) {
            baseDate = LocalDate.parse(basePointInTime);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            baseDate = LocalDate.parse(basePointInTime, formatter);
        }

        return new SyncJob(
                jobType,
                baseDate,
                clientIp,
                Instant.now(),
                status,
                indexInfo
        );
    }

    private List<StockIndexInfoResult> filterNewIndexInfoResults(
            Map<String, StockIndexInfoResult> indexInfosFromOpenAPI,
            List<IndexInfo> existingIndexInfos
    ) {
        Set<String> existingKeys = existingIndexInfos.stream()
                .map(indexInfo -> indexInfo.getIndexInfoBusinessKey().toString())
                .collect(Collectors.toSet());

        return indexInfosFromOpenAPI.entrySet()
                .stream()
                .filter(info -> !existingKeys.contains(info.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}