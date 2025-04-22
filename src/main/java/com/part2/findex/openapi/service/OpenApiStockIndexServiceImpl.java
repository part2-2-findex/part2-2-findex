package com.part2.findex.openapi.service;

import com.part2.findex.openapi.client.StockIndexApiClient;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenApiStockIndexServiceImpl implements OpenApiStockIndexService {

  private final StockIndexApiClient apiClient;

  public StockIndexResponse getStockIndexData(StockIndexRequestParam param) {
    return apiClient.fetchStockIndices(param);
  }
}
