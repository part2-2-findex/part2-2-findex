package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncService {
    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final EntityManager entityManager;

    public IndexInfo updateIndexInfo(IndexInfo existingIndexInfo, StockIndexInfoResult updated) {
        if (updated == null) return null;

        existingIndexInfo.update(
                updated.employedItemsCount(),
                updated.basePointInTime(),
                updated.baseIndex(),
                null,
                BASE_INDEX_TOLERANCE
        );

        return existingIndexInfo;
    }

    public IndexInfo saveNewIndexInfo(StockIndexInfoResult newIndexInfo) {
        IndexInfo indexInfo = convertToIndexInfo(newIndexInfo);
        entityManager.persist(indexInfo);
        return indexInfo;
    }

    private IndexInfo convertToIndexInfo(StockIndexInfoResult stockIndexInfo) {
        return new IndexInfo(
                stockIndexInfo.indexClassification(),
                stockIndexInfo.indexName(),
                stockIndexInfo.employedItemsCount(),
                stockIndexInfo.basePointInTime(),
                stockIndexInfo.baseIndex(),
                false);
    }
}