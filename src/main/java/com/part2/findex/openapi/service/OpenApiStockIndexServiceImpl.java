package com.part2.findex.openapi.service;

import com.part2.findex.openapi.client.StockIndexApiClient;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.syncjob.dto.IndexDataOpenAPIRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenApiStockIndexServiceImpl implements OpenApiStockIndexService {

    private final StockIndexApiClient apiClient;

    @Value("${public-data.api.key}")
    private String serviceKey;

    public StockIndexResponse getStockIndexData(StockIndexRequestParam param) {
        return apiClient.fetchStockIndices(param);
    }

    @Override
    public List<StockIndexInfoResult> getAllLastDateIndexInfoFromOpenAPI(LocalDate targetDate) {
        StockIndexResponse stockIndexes = getStockIndicesByDate(0, targetDate);
        int totalCount = stockIndexes.response()
                .body()
                .totalCount();
        StockIndexResponse totalStockIndex = getStockIndicesByDate(totalCount, targetDate);

        return totalStockIndex.response().body().items()
                .item()
                .stream()
                .map(StockIndexInfoResult::from)
                .toList();
    }

    @Override
    public List<StockDataResult> getAllIndexDataBetweenDates(List<IndexDataOpenAPIRequest> openAPIRequests) {
        List<StockDataResult> results = new ArrayList<>();

        for (IndexDataOpenAPIRequest openAPIRequest : openAPIRequests) {
            StockIndexResponse stockIndicesBetweenDateForTotalCount = getStockIndicesBetweenDate(
                    0,
                    openAPIRequest.indexName(),
                    openAPIRequest.startDate(),
                    openAPIRequest.endDate().plusDays(1)
            );

            int totalCount = stockIndicesBetweenDateForTotalCount.response()
                    .body()
                    .totalCount();
            StockIndexResponse stockIndicesBetweenDate = getStockIndicesBetweenDate(
                    totalCount,
                    openAPIRequest.indexName(),
                    openAPIRequest.startDate(),
                    openAPIRequest.endDate().plusDays(1)
            );

            List<StockDataResult> stockDatas = stockIndicesBetweenDate.response().body().items().item()
                    .stream()
                    .map(StockDataResult::from)
                    .toList();

            results.addAll(stockDatas);
        }

        return results;
    }

    private StockIndexResponse getStockIndicesBetweenDate(int numbersOfRow, String indexName, LocalDate startDate, LocalDate untilDate) {
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedUntilDate = untilDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StockIndexRequestParam getTotalDataParam = StockIndexRequestParam.builder()
                .serviceKey(serviceKey)
                .pageNo(1)
                .numOfRows(numbersOfRow)
                .idxNm(indexName)
                .beginBasDt(formattedStartDate)
                .endBasDt(formattedUntilDate)
                .build();

        return apiClient.fetchStockIndices(getTotalDataParam);
    }


    private StockIndexResponse getStockIndicesByDate(int numbersOfRow, LocalDate targetDate) {
        String formattedTargetDate = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StockIndexRequestParam getTotalDataParam = StockIndexRequestParam.builder()
                .serviceKey(serviceKey)
                .pageNo(1)
                .numOfRows(numbersOfRow)
                .basDt(formattedTargetDate)
                .build();

        return apiClient.fetchStockIndices(getTotalDataParam);
    }
}
