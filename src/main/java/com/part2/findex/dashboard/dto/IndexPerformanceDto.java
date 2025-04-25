package com.part2.findex.dashboard.dto;

import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
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

  public IndexPerformanceDto(Long indexInfoId, IndexInfoBusinessKey key,
      Double versus, Double fluctuationRate,
      Double currentPrice, Double beforePrice) {
    this.indexInfoId = indexInfoId;
    this.indexClassification = key.getIndexClassification();
    this.indexName = key.getIndexName();
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.currentPrice = currentPrice;
    this.beforePrice = beforePrice;
  }
}