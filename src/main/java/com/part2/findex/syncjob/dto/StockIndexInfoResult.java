package com.part2.findex.syncjob.dto;

import com.part2.findex.openapi.dto.StockItem;

public record StockIndexInfoResult(
        String indexClassification,
        String indexName,
        int employedItemsCount,
        String basePointInTime,
        double baseIndex
) {

    public static StockIndexInfoResult from(StockItem stockItem) {
        return new StockIndexInfoResult(
                stockItem.idxCsf(),
                stockItem.idxNm(),
                stockItem.epyItmsCnt(),
                stockItem.basPntm(),
                stockItem.basIdx()
        );
    }
}
