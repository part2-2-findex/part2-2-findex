package com.part2.findex.openapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record StockDataResult(
        String indexName,
        String indexClassification,
        LocalDate baseDate,
        BigDecimal marketPrice,
        BigDecimal closingPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        Long tradingQuantity,
        BigDecimal tradingPrice,
        BigDecimal marketTotalAmount
) {

    public static StockDataResult from(StockItem stockItem) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate baseDate = LocalDate.parse(stockItem.basDt(), formatter);
        
        return new StockDataResult(
                stockItem.idxNm(),
                stockItem.idxCsf(),
                baseDate,
                BigDecimal.valueOf(stockItem.mkp()),
                BigDecimal.valueOf(stockItem.clpr()),
                BigDecimal.valueOf(stockItem.hipr()),
                BigDecimal.valueOf(stockItem.lopr()),
                BigDecimal.valueOf(stockItem.vs()),
                BigDecimal.valueOf(stockItem.fltRt()),
                stockItem.trqu(),
                BigDecimal.valueOf(stockItem.trPrc()),
                BigDecimal.valueOf(stockItem.lstgMrktTotAmt())
        );
    }
}
