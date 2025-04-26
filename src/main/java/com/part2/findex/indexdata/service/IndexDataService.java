package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import java.time.LocalDate;

public interface IndexDataService {
    void updateIndexData(Long indexDataId, IndexDataUpdateRequest indexDataUpdateRequest);

    void deleteIndexData(Long indexDataId);

    IndexDataDto createIndexData(IndexDataCreateRequest indexDataCreateRequest);

    byte[] getCsvData(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        String sortField,
        String sortDirection);
}
