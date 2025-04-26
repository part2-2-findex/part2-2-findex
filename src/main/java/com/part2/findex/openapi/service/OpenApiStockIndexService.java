package com.part2.findex.openapi.service;

import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.service.IndexDataSyncRequestOpenAPI;

import java.util.List;
import java.util.Map;

public interface OpenApiStockIndexService {
    StockIndexResponse getStockIndexData(StockIndexRequestParam param);

    Map<String, StockIndexInfoResult> getAllLastDateIndexInfoFromOpenAPI();

    List<StockDataResult> getAllIndexDataBetweenDates(List<IndexDataSyncRequestOpenAPI> openAPIRequests);
}
