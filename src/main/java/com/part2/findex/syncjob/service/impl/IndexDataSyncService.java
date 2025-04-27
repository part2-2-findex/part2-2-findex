package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexDataSyncService {
    private final EntityManager entityManager;
    private final SyncJobRepository syncJobRepository;

    public IndexData saveNewIndexData(StockDataResult stockDataResult, IndexInfo indexInfo) {
        IndexData indexData = convertToIndexData(stockDataResult, indexInfo);
        entityManager.persist(indexData);

        return indexData;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedIndexDataSyncJob(SyncJob syncJob) {
        syncJobRepository.save(syncJob);
    }

    public IndexData convertToIndexData(StockDataResult stockDataResult, IndexInfo indexInfo) {
        return new IndexData(
                indexInfo,
                stockDataResult.baseDate(),
                SourceType.OPEN_API,
                stockDataResult.marketPrice(),
                stockDataResult.closingPrice(),
                stockDataResult.highPrice(),
                stockDataResult.lowPrice(),
                stockDataResult.versus(),
                stockDataResult.fluctuationRate(),
                stockDataResult.tradingQuantity(),
                stockDataResult.tradingPrice(),
                stockDataResult.marketTotalAmount());
    }
}
