package com.part2.findex.indexdata.service.strategy;

import com.part2.findex.indexdata.entity.IndexData;

import java.time.LocalDate;
import java.util.List;

public interface SortedCallback<T> {
    List<IndexData> call(Long indexInfoId, LocalDate startDate, LocalDate endDate, T cursor, Long idCursor);
}
