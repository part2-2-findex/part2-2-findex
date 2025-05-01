package com.part2.findex.openapi.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.openapi.client.StockIndexApiClient;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.openapi.dto.StockItem;
import com.part2.findex.syncjob.dto.IndexDataOpenAPIRequest;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.service.common.TargetDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OpenApiStockIndexService {

    @Value("${public-data.api.key}")
    private String serviceKey;
    private final StockIndexApiClient apiClient;
    private final TargetDateService targetDateService;

    public List<StockIndexInfoResult> loadAllLastDateIndexInfoFromOpenAPI() {
        LocalDate latestBusinessDay = targetDateService.getLatestBusinessDay();
        return getAllStockIndicesFromOpenAPIBetweenDate(null, latestBusinessDay, latestBusinessDay).stream()
                .map(StockIndexInfoResult::from)
                .toList();
    }

    public List<StockDataResult> loadIndexDataFromOpenAPI(IndexDataSyncRequest indexDataSyncRequest, List<IndexInfo> requestedIndexInfos) {
        List<IndexDataOpenAPIRequest> indexDataRequestByName = requestedIndexInfos.stream()
                .map(indexInfo -> IndexDataOpenAPIRequest.of(indexInfo, indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo()))
                .toList();

        return getAllIndexDataBetweenDates(indexDataRequestByName);
    }

    private List<StockDataResult> getAllIndexDataBetweenDates(List<IndexDataOpenAPIRequest> openAPIRequests) {
        List<StockDataResult> results = new ArrayList<>();
        for (IndexDataOpenAPIRequest openAPIRequest : openAPIRequests) {
            List<StockDataResult> stockDataResults = getAllStockIndicesFromOpenAPIBetweenDate(openAPIRequest.indexName(), openAPIRequest.startDate(), openAPIRequest.endDate()).stream()
                    .map(StockDataResult::from)
                    .toList();

            results.addAll(stockDataResults);
        }

        return results;
    }

    private List<StockItem> getAllStockIndicesFromOpenAPIBetweenDate(String indexName, LocalDate startDate, LocalDate endDate) {
        LocalDate untilDate = endDate.plusDays(1);
        StockIndexResponse stockIndexes = getStockIndicesBetweenDate(0, indexName, startDate, untilDate);
        int totalCount = stockIndexes.response().body().totalCount();
        StockIndexResponse totalStockIndex = getStockIndicesBetweenDate(totalCount, indexName, startDate, untilDate);

        return totalStockIndex.response().body().items()
                .item()
                .stream()
                .toList();
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

}
