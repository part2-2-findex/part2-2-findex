package com.part2.findex.dashboard.dto;

public record IndexPerformanceDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    Double versus,
    Double fluctuationRate,
    Double currentPrice,
    Double beforePrice
) {
}
