package com.part2.findex.dashboard.dto;

public record RankedIndexPerformanceDto(
    IndexPerformanceDto performance,
    Integer rank
) {
}
