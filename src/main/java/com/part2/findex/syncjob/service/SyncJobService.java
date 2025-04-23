package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBussinessKey;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class SyncJobService {
    private static final int BATCH_SIZE = 50;
    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final EntityManager entityManager;

    // TODO: 4/23/25 밑에 있는거랑 합쳐야됨
    public <T, U> void processBatchTemplateOrigin(List<T> items, Function<T, U> callback) {
        // 성공 기록을 언제 flush 할것인가
        int count = 0;
        for (T item : items) {
            //// 히스토리
            try {
                callback.apply(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (++count % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        flushAndClear();
    }

    // TODO: 4/23/25 IP 넣어줘야함, IndexInfo도 U로 바꿔줘야함
    public <T> List<SyncJob> processBatchTemplate(List<T> items, Function<T, IndexInfo> callback) {
        // 성공 기록을 언제 flush 할것인가
        LocalDate yesterday = LocalDate.now();
        List<SyncJob> syncJobs = new ArrayList<>();
        int count = 0;
        for (T item : items) {
            //// 히스토리
            IndexInfo applied = null;
            try {
                applied = callback.apply(item);
                ////
                SyncJob syncJob = new SyncJob(
                        SyncJobType.INDEX_INFO,
                        yesterday,
                        "IP",
                        Instant.now(),
                        SyncJobStatus.SUCCESS,
                        applied
                );
                syncJobs.add(syncJob);
                ////
            } catch (Exception e) {
                ////
                SyncJob syncJob = new SyncJob(
                        SyncJobType.INDEX_INFO,
                        yesterday,
                        "IP",
                        Instant.now(),
                        SyncJobStatus.FAILED,
                        applied
                );
                syncJobs.add(syncJob);
            }


            if (++count % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        flushAndClear();

        return syncJobs;
    }

    public Function<IndexInfo, IndexInfo> createUpdateFunction(
            Map<IndexInfoBussinessKey, StockIndexInfoResult> indexInfosFromOpenAPI
    ) {
        return existingIndexInfo -> {
            StockIndexInfoResult updated = indexInfosFromOpenAPI.get(existingIndexInfo.getIndexInfoBussinessKey());
            if (updated == null) return null;

            existingIndexInfo.update(
                    updated.employedItemsCount(),
                    updated.basePointInTime(),
                    updated.baseIndex(),
                    null,
                    BASE_INDEX_TOLERANCE
            );

            return existingIndexInfo;
        };
    }

    public Function<StockIndexInfoResult, IndexInfo> createSaveFunction() {
        return onlyNewStockIndex -> {
            IndexInfo indexInfo = convertToIndexInfo(onlyNewStockIndex);
            entityManager.persist(indexInfo);

            return indexInfo;
        };
    }

    public Function<SyncJob, Void> createSyncJodSaveFunction() {
        return syncJob -> {
            entityManager.persist(syncJob);
            return null;
        };
    }

    private IndexInfo convertToIndexInfo(StockIndexInfoResult onlyNewStockIndex) {
        return new IndexInfo(
                onlyNewStockIndex.indexClassification(),
                onlyNewStockIndex.indexName(),
                onlyNewStockIndex.employedItemsCount(),
                onlyNewStockIndex.basePointInTime(),
                onlyNewStockIndex.baseIndex(),
                false);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}