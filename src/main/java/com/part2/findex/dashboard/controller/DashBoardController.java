package com.part2.findex.dashboard.controller;

import com.part2.findex.dashboard.dto.IndexChartDto;
import com.part2.findex.dashboard.dto.IndexPerformanceDto;
import com.part2.findex.dashboard.service.DashBoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class DashBoardController {
  private final DashBoardService dashBoardService;

  @GetMapping("/performance/favorite")
  ResponseEntity<List<IndexPerformanceDto>> getFavoriteIndexPerformance(
      @RequestParam("periodType") String periodType
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(dashBoardService.getFavoriteIndexPerformance(periodType));
  }

  @GetMapping("/{id}/chart")
  ResponseEntity<IndexChartDto> getIndexChart(
      @PathVariable Long id,
      @RequestParam("periodType") String periodType
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(dashBoardService.getIndexChart(id, periodType));
  }
}
