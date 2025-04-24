package com.part2.findex.dashboard.util;

import com.part2.findex.dashboard.dto.ChartDataPoint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MovingAverageCalculator {
  public static List<ChartDataPoint> calculate(List<ChartDataPoint> dataPoints, int period) {
    List<ChartDataPoint> result = new ArrayList<>();
    Deque<Double> window = new ArrayDeque<>();
    double sum = 0.0;

    for (int i = 0; i < dataPoints.size(); i++) {
      ChartDataPoint point = dataPoints.get(i);
      Double value = point.value();

      // 윈도우에 값 추가
      window.addLast(value);
      sum += value;

      // 윈도우 사이즈가 period를 초과하면 맨 앞 제거
      if (window.size() > period) {
        sum -= window.removeFirst();
      }

      // period만큼 누적된 이후부터 평균 계산
      if (window.size() == period) {
        double average = sum / period;
        BigDecimal rounded = new BigDecimal(average).setScale(2, RoundingMode.HALF_UP);
        result.add(new ChartDataPoint(point.date(), rounded.doubleValue()));
      }
    }

    return result;
  }
}
