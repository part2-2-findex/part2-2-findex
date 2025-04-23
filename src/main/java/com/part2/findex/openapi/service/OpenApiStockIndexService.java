package com.part2.findex.openapi.service;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;

import java.util.Map;

public interface OpenApiStockIndexService {
    StockIndexResponse getStockIndexData(StockIndexRequestParam param);

    Map<String, StockIndexInfoResult> getAllIndexInfoFromOpenAPI();
}
