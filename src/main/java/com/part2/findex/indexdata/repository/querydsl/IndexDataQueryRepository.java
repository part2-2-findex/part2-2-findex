package com.part2.findex.indexdata.repository.querydsl;

import com.part2.findex.indexdata.dto.CsvExportDto;
import java.time.LocalDate;
import java.util.List;

public interface IndexDataQueryRepository {
  List<CsvExportDto> getCsvData(Long indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection);
}
