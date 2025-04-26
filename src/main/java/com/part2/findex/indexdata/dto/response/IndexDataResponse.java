package com.part2.findex.indexdata.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class IndexDataResponse {
    private Long id;                    // indexData ID
    private Long indexInfoId;           // indexInfo ID
    private LocalDate baseDate;         // baseDate (날짜 형식)
    private String sourceType;          // sourceType (OPEN_API 등)
    private BigDecimal marketPrice;     // marketPrice
    private BigDecimal closingPrice;    // closingPrice
    private BigDecimal highPrice;       // highPrice
    private BigDecimal lowPrice;        // lowPrice
    private BigDecimal versus;          // versus
    private BigDecimal fluctuationRate; // fluctuationRate
    private Long tradingQuantity;       // tradingQuantity
    private BigDecimal tradingPrice;    // tradingPrice
    private BigDecimal marketTotalAmount; // marketTotalAmount
}
