package com.part2.findex.openapi.service;

import com.part2.findex.openapi.client.StockIndexApiClient;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.indexinfo.entity.IndexInfoBussinessKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenApiStockIndexServiceImpl implements OpenApiStockIndexService {

    private final StockIndexApiClient apiClient;

    @Value("${public-data.api.key}")
    private String serviceKey;

    public StockIndexResponse getStockIndexData(StockIndexRequestParam param) {
        return apiClient.fetchStockIndices(param);
    }

    @Override
    public Map<IndexInfoBussinessKey, StockIndexInfoResult> getAllIndexInfoFromOpenAPI() {
        StockIndexResponse stockIndexes = getStockIndicesByDate(0);
        int totalCount = stockIndexes.response()
                .body()
                .totalCount();
        StockIndexResponse totalStockIndex = getStockIndicesByDate(totalCount);


        return totalStockIndex.response().body().items()
                .item()
                .stream()
                .map(StockIndexInfoResult::from)
                .collect(Collectors.toMap(
                        IndexInfoBussinessKey::from,
                        Function.identity()
                ));
    }

    private StockIndexResponse getStockIndicesByDate(int totalNumber) {
        String yesterday = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StockIndexRequestParam getTotalDataParam = StockIndexRequestParam.builder()
                .serviceKey(serviceKey)
                .pageNo(1)
                .numOfRows(totalNumber)
                .basDt(yesterday)
                .build();

        return apiClient.fetchStockIndices(getTotalDataParam);
    }
}
