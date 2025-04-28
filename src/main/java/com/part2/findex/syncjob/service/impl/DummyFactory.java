package com.part2.findex.syncjob.service.impl;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DummyFactory {

    public IndexInfo createDummyIndexInfoFromStockData(StockDataResult stockDataResult) {
        return new IndexInfo(
                stockDataResult.indexClassification(),
                stockDataResult.indexName(),
                0,
                null,
                0,
                false,
                null
        );
    }

    public IndexInfo createDummyIndexInfo(StockIndexInfoResult stockIndexInfoResult) {
        return new IndexInfo(
                stockIndexInfoResult.indexClassification(),
                stockIndexInfoResult.indexName(),
                0,
                null,
                0,
                false,
                null
        );
    }

    public SyncJob createDummyIndexDataSyncJob(StockDataResult stockDataResult, IndexInfo indexInfo) {
        return new SyncJob(
                SyncJobType.INDEX_DATA,
                stockDataResult.baseDate(),
                null,
                null,
                SyncJobStatus.SUCCESS,
                indexInfo
        );
    }

    public SyncJob createDummyIndexInfoSyncJob(StockIndexInfoResult stockIndexInfoResult, IndexInfo IndexInfo) {
        return new SyncJob(
                SyncJobType.INDEX_INFO,
                LocalDate.parse(stockIndexInfoResult.baseDateTime(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                null,
                null,
                SyncJobStatus.SUCCESS,
                IndexInfo
        );
    }
}
