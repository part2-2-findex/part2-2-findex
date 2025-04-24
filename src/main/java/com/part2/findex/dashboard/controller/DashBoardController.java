package com.part2.findex.dashboard.controller;

import com.part2.findex.dashboard.dto.IndexPerformanceDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-data")
public class DashBoardController {

  @GetMapping("/performance/favorite")
  ResponseEntity<List<IndexPerformanceDto>> getFavoriteIndexPerformance(
      @RequestParam("periodType") String periodType
  ) {

  }
}
