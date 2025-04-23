package com.part2.findex.openapi.service;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.indexinfo.entity.IndexInfoBussinessKey;

import java.util.Map;

public interface OpenApiStockIndexService {
    StockIndexResponse getStockIndexData(StockIndexRequestParam param);

    // TODO: 4/23/25 도메인키 노출 수정필요
    Map<IndexInfoBussinessKey, StockIndexInfoResult> getAllIndexInfoFromOpenAPI();
}
