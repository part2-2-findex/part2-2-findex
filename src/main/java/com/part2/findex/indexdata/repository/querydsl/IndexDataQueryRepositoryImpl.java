package com.part2.findex.indexdata.repository.querydsl;

import com.part2.findex.indexdata.dto.CsvExportDto;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.entity.QIndexData;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexDataQueryRepositoryImpl implements IndexDataQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<CsvExportDto> getCsvData(Long indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection) {
    QIndexData i = QIndexData.indexData;
    // 동적 정렬 처리
    OrderSpecifier<?> order = getOrderSpecifier(i, sortField, sortDirection);

    return queryFactory
        .select(Projections.constructor(CsvExportDto.class,
            i.baseDate,
            i.marketPrice,
            i.closingPrice,
            i.highPrice,
            i.lowPrice,
            i.tradingQuantity,
            i.versus,
            i.fluctuationRate
        ))
        .from(i)
        .where(
            i.indexInfo.id.eq(indexInfoId),
            i.baseDate.between(startDate, endDate)
        )
        .orderBy(order)
        .fetch();
  }

  private OrderSpecifier<?> getOrderSpecifier(QIndexData i, String field, String direction) {
    PathBuilder<IndexData> path = new PathBuilder<>(IndexData.class, "indexData");
    Order order = direction.equalsIgnoreCase("DESC") ? Order.DESC : Order.ASC;
    return new OrderSpecifier<>(order, path.getComparable(field, Comparable.class));
  }
}
