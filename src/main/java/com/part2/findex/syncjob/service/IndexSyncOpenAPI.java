package com.part2.findex.syncjob.service;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.openapi.service.OpenApiStockIndexService;
import com.part2.findex.syncjob.dto.IndexDataOpenAPIRequest;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.service.common.TargetDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class IndexSyncOpenAPI {
    private final OpenApiStockIndexService openApiStockIndexService;
    private final TargetDateService targetDateService;

    public List<StockIndexInfoResult> loadAllLastDateIndexInfoFromOpenAPI() {
        return openApiStockIndexService.getAllLastDateIndexInfoFromOpenAPI(targetDateService.getLatestBusinessDay());
    }

    public List<StockDataResult> loadIndexDataFromOpenAPI(IndexDataSyncRequest indexDataSyncRequest, List<IndexInfo> requestedIndexInfos) {
        List<IndexDataOpenAPIRequest> indexDataRequestByName = requestedIndexInfos.stream()
                .map(indexInfo -> IndexDataOpenAPIRequest.of(indexInfo, indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo()))
                .toList();

        return openApiStockIndexService.getAllIndexDataBetweenDates(indexDataRequestByName);
    }
}
