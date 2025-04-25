package com.part2.findex.dashboard.dto;

import java.util.List;

public record IndexChartDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    String periodType,
    List<ChartDataPoint> dataPoints,
    List<ChartDataPoint> ma5DataPoints,
    List<ChartDataPoint> ma20DataPoints
) {
}
