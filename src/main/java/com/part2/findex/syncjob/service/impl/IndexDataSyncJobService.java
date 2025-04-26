package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.mapper.IndexInfoMapper;
import com.part2.findex.syncjob.service.IndexDataSyncRequestOpenAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexDataSyncJobService {
    private final IndexInfoRepository indexInfoRepository;
    private final OpenApiStockIndexService openApiStockIndexService;
    private final IndexSyncBatchService indexSyncBatchService;
    private final ClientIpResolver clientIpResolver;
    private final IndexDataSyncService indexDataSyncService;

    public Map<Long, List<LocalDate>> findMissingIndexDates(
            IndexDataSyncRequest request,
            Map<Long, List<SyncJob>> existingIndexDataSyncJob
    ) {
        Map<Long, List<LocalDate>> missingDatesByIndexId = new HashMap<>();

        for (Long indexInfoId : request.indexInfoIds()) {
            List<SyncJob> syncJobs = existingIndexDataSyncJob.get(indexInfoId);

            List<LocalDate> fullDateRange = request.baseDateFrom()
                    .datesUntil(request.baseDateTo().plusDays(1))
                    .toList();

            if (syncJobs == null) {
                missingDatesByIndexId.put(indexInfoId, fullDateRange);
                continue;
            }

            Set<LocalDate> existingDates = syncJobs.stream()
                    .map(SyncJob::getTargetDate)
                    .collect(Collectors.toSet());

            List<LocalDate> missingDates = fullDateRange.stream()
                    .filter(date -> !existingDates.contains(date))
                    .toList();

            if (!missingDates.isEmpty()) {
                missingDatesByIndexId.put(indexInfoId, missingDates);
            }
        }

        return missingDatesByIndexId;
    }

    public List<StockDataResult> createOpenAPIRequestsFromMissingDates(
            Map<Long, List<LocalDate>> missingIndexDates,
            List<IndexInfo> allIndexInfoById
    ) {
        Map<Long, IndexInfo> indexInfosById = allIndexInfoById
                .stream()
                .collect(Collectors.toMap(IndexInfo::getId, Function.identity()));

        List<IndexDataSyncRequestOpenAPI> openAPIRequests = new ArrayList<>();
        for (Map.Entry<Long, List<LocalDate>> entry : missingIndexDates.entrySet()) {
            List<LocalDate> missingDates = entry.getValue();
            Long indexId = entry.getKey();
            IndexInfo indexInfo = indexInfosById.get(indexId);

            if (indexInfo != null) {
                LocalDate minDate = Collections.min(missingDates);
                LocalDate maxDatePlusOne = Collections.max(missingDates).plusDays(1);
                openAPIRequests.add(new IndexDataSyncRequestOpenAPI(
                        indexInfo.getIndexName(),
                        minDate,
                        maxDatePlusOne
                ));
            }
        }

        return openApiStockIndexService.getAllIndexDataBetweenDates(openAPIRequests);
    }

    public List<IndexInfo> fetchIndexInfosForMissingDates(Map<Long, List<LocalDate>> missingIndexDates) {
        List<Long> missingIds = new ArrayList<>(missingIndexDates.keySet());
        return indexInfoRepository.findAllById(missingIds);
    }

    public List<SyncJob> createSyncJobsForNewIndexData(
            List<IndexInfo> indexInfos,
            List<StockDataResult> stockDataResults
    ) {
        Map<IndexInfoBusinessKey, IndexInfo> infoBusinessKeyIndexInfoMap = indexInfos.stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexInfoBusinessKey,
                        Function.identity()
                ));
        System.out.println(stockDataResults);
        return indexSyncBatchService.processBatch(stockDataResults, stockDataResult -> {
            SyncJobStatus status;
            IndexData indexData = null;
            try {
                IndexInfo indexInfo = infoBusinessKeyIndexInfoMap.get(IndexInfoMapper.toIndexInfoBusinessKey(stockDataResult));
                indexData = indexDataSyncService.saveNewIndexData(stockDataResult, indexInfo);
                status = SyncJobStatus.SUCCESS;
            } catch (Exception e) {
                status = SyncJobStatus.FAILED;
            }

            assert indexData != null;
            return createSyncJobIndexData(SyncJobType.INDEX_DATA, indexData, status);
        });
    }

    private SyncJob createSyncJobIndexData(SyncJobType jobType, IndexData indexData, SyncJobStatus status) {
        String clientIp = clientIpResolver.getClientIp();
        String basePointInTime = indexData.getBaseDate().toString();
        LocalDate baseDate = getLocalDate(basePointInTime);

        return new SyncJob(
                jobType,
                baseDate,
                clientIp,
                Instant.now(),
                status,
                indexData.getIndexInfo()
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
