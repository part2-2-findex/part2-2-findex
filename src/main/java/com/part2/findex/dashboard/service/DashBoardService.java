package com.part2.findex.dashboard.service;

import com.part2.findex.dashboard.dto.IndexChartDto;
import com.part2.findex.dashboard.dto.IndexPerformanceDto;
import java.util.List;

public interface DashBoardService {
  List<IndexPerformanceDto> getFavoriteIndexPerformance(String periodType);
  IndexChartDto getIndexChart(Long id, String periodType);
}
