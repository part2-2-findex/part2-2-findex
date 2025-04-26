package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;

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
