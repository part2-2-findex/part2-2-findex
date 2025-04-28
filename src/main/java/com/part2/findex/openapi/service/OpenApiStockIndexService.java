package com.part2.findex.openapi.service;

import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.IndexDataOpenAPIRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;

import java.time.LocalDate;
import java.util.List;

public interface OpenApiStockIndexService {
    StockIndexResponse getStockIndexData(StockIndexRequestParam param);

    List<StockIndexInfoResult> getAllLastDateIndexInfoFromOpenAPI(LocalDate targetDate);

    List<StockDataResult> getAllIndexDataBetweenDates(List<IndexDataOpenAPIRequest> openAPIRequests);
}
