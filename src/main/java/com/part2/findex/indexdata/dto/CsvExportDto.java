package com.part2.findex.indexdata.dto;

import com.opencsv.bean.CsvBindByPosition;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class CsvExportDto {
  @CsvBindByPosition(position = 0)
  private LocalDate baseDate;

  @CsvBindByPosition(position = 1)
  private BigDecimal marketPrice;

  @CsvBindByPosition(position = 2)
  private BigDecimal closingPrice;

  @CsvBindByPosition(position = 3)
  private BigDecimal highPrice;

  @CsvBindByPosition(position = 4)
  private BigDecimal lowPrice;

  @CsvBindByPosition(position = 5)
  private Long tradingQuantity;

  @CsvBindByPosition(position = 6)
  private BigDecimal versus;

  @CsvBindByPosition(position = 7)
  private BigDecimal fluctuationRate;

  public CsvExportDto(
      LocalDate baseDate,
      BigDecimal marketPrice,
      BigDecimal closingPrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      Long tradingQuantity,
      BigDecimal versus,
      BigDecimal fluctuationRate
      ) {
    this.baseDate = baseDate;
    this.marketPrice = marketPrice;
    this.closingPrice = closingPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.tradingQuantity = tradingQuantity;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
  }
}
