package com.part2.findex.dashboard.dto;

import lombok.Getter;

@Getter
public class IndexPerformanceDto {
  private Long indexInfoId;
  private String indexClassification;
  private String indexName;
  private Double versus;
  private Double fluctuationRate;
  private Double currentPrice;
  private Double beforePrice;

  public IndexPerformanceDto(Long indexInfoId, String indexClassification, String indexName,
      Double versus, Double fluctuationRate,
      Double currentPrice, Double beforePrice) {
    this.indexInfoId = indexInfoId;
    this.indexClassification = indexClassification;
    this.indexName = indexName;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.currentPrice = currentPrice;
    this.beforePrice = beforePrice;
  }
}