package com.part2.findex.dashboard.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ChartDataPoint {

  private LocalDate date;
  private Double value;

  public ChartDataPoint(LocalDate date, Double value) {
    this.date = date;
    this.value = value;
  }
}