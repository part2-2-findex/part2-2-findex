package com.part2.findex.openapi.service;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;

public interface OpenApiStockIndexService {
  StockIndexResponse getStockIndexData(StockIndexRequestParam param);
}
