package com.part2.findex.dashboard.service;

import com.part2.findex.dashboard.dto.ChartDataPoint;
import com.part2.findex.dashboard.dto.IndexChartDto;
import com.part2.findex.dashboard.dto.IndexPerformanceDto;
import com.part2.findex.dashboard.dto.RankedIndexPerformanceDto;
import com.part2.findex.dashboard.repository.DashBoardIndexDataRepository;
import com.part2.findex.dashboard.repository.DashBoardIndexInfoRepository;
import com.part2.findex.dashboard.util.MovingAverageCalculator;
import com.part2.findex.indexinfo.entity.IndexInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {
  private final DashBoardIndexDataRepository dashBoardIndexDataRepository;
  private final DashBoardIndexInfoRepository dashBoardIndexInfoRepository;


  @Override
  public List<IndexPerformanceDto> getFavoriteIndexPerformance(String periodType) {
    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = currentDate.minusDays(1);
    if (periodType.equals("WEEKLY")) {
      pastDate = pastDate.minusWeeks(1);
    } else if (periodType.equals("MONTHLY")) {
      pastDate = pastDate.minusMonths(1);
    }
    return dashBoardIndexDataRepository.findIndexPerformances(currentDate, pastDate);
  }

  @Override
  public IndexChartDto getIndexChart(Long id, String periodType) {
    IndexInfo indexInfo = dashBoardIndexInfoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("IndexInfo not found"));

    String classification = indexInfo.getIndexClassification();
    String name = indexInfo.getIndexName();

    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = currentDate.minusMonths(1);
    if (periodType.equals("QUARTERLY")) {
      pastDate = pastDate.minusMonths(3);
    } else if (periodType.equals("YEARLY")) {
      pastDate = pastDate.minusYears(1);
    }

    List<ChartDataPoint> mainData = dashBoardIndexDataRepository.findChartDataPoints(id, currentDate, pastDate);
    List<ChartDataPoint> ma5 = MovingAverageCalculator.calculate(mainData, 5);
    List<ChartDataPoint> ma20 = MovingAverageCalculator.calculate(mainData, 20);

    return new IndexChartDto(id, classification, name, periodType, mainData, ma5, ma20);
  }

  @Override
  public List<RankedIndexPerformanceDto> getRankedIndexPerformance(Long id, String periodType,
      int limit) {

    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = currentDate.minusDays(1);
    if (periodType.equals("WEEKLY")) {
      pastDate = pastDate.minusWeeks(1);
    } else if (periodType.equals("MONTHLY")) {
      pastDate = pastDate.minusMonths(1);
    }

    List<IndexPerformanceDto> performances = dashBoardIndexDataRepository.findAllIndexPerformances(currentDate, pastDate);
    List<RankedIndexPerformanceDto> ranked = IntStream.range(0, performances.size())
        .mapToObj(i -> new RankedIndexPerformanceDto(performances.get(i), i + 1)) // rank는 1부터 시작
        .toList();

    if (id != null) {
      return ranked.stream()
          .filter(r -> r.performance().getIndexInfoId().equals(id))
          .findFirst()
          .map(List::of)
          .orElseGet(List::of);
    }
    return limit > 0
        ? new ArrayList<>(ranked.subList(0, Math.min(limit, ranked.size())))
        : ranked;
  }
}
