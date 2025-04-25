package com.part2.findex.dashboard.repository;

import com.part2.findex.dashboard.dto.ChartDataPoint;
import com.part2.findex.dashboard.dto.IndexPerformanceDto;
import com.part2.findex.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashBoardIndexDataRepository extends JpaRepository<IndexData, Long> {
  @Query("""
    SELECT new com.part2.findex.dashboard.dto.IndexPerformanceDto(
        i.id,
        i.indexClassification,
        i.indexName,
        CAST(d1.closePrice - d2.closePrice AS double),
        CAST(((d1.closePrice - d2.closePrice) / d2.closePrice) * 100 AS double),
        CAST(d1.closePrice AS double),
        CAST(d2.closePrice AS double)
    )
    FROM IndexData d1
    JOIN d1.indexInfo i
    JOIN IndexData d2 ON d1.indexInfo = d2.indexInfo
    WHERE i.favorite = true
      AND d1.tradeDate = :currentDate
      AND d2.tradeDate = :pastDate
    ORDER BY ((d1.closePrice - d2.closePrice) / d2.closePrice) * 100 DESC
""")
  List<IndexPerformanceDto> findIndexPerformances(
      @Param("currentDate") LocalDate currentDate,
      @Param("pastDate") LocalDate pastDate
  );

  @Query("""
    SELECT new com.part2.findex.dashboard.dto.ChartDataPoint(
        d.tradeDate,
        CAST(d.closePrice AS double)
    )
    FROM IndexData d
    WHERE d.indexInfo.id = :indexInfoId
      AND d.tradeDate BETWEEN :pastDate AND :currentDate
    ORDER BY d.tradeDate DESC
""")
  List<ChartDataPoint> findChartDataPoints(
      @Param("indexInfoId") Long indexInfoId,
      @Param("currentDate") LocalDate currentDate,
      @Param("pastDate") LocalDate pastDate
  );

  @Query("""
    SELECT new com.part2.findex.dashboard.dto.IndexPerformanceDto(
        i.id,
        i.indexClassification,
        i.indexName,
        CAST(d1.closePrice - d2.closePrice AS double),
        CAST(((d1.closePrice - d2.closePrice) / d2.closePrice) * 100 AS double),
        CAST(d1.closePrice AS double),
        CAST(d2.closePrice AS double)
    )
    FROM IndexData d1
    JOIN d1.indexInfo i
    JOIN IndexData d2 ON d1.indexInfo = d2.indexInfo
    WHERE d1.tradeDate = :currentDate
      AND d2.tradeDate = :pastDate
    ORDER BY ((d1.closePrice - d2.closePrice) / d2.closePrice) * 100 DESC
""")
  List<IndexPerformanceDto> findAllIndexPerformances(
      @Param("currentDate") LocalDate currentDate,
      @Param("pastDate") LocalDate pastDate
  );

}