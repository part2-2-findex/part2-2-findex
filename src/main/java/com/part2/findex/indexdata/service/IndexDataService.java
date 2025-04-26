package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexinfo.dto.response.PageResponse;

import java.time.LocalDate;

public interface IndexDataService {

    PageResponse<IndexDataResponse> findAllBySearchItem(IndexDataRequest indexSearchRequest);

    IndexDataResponse updateIndexData(Long indexDataId, IndexDataUpdateRequest indexDataUpdateRequest);

    void deleteIndexData(Long indexDataId);

    IndexDataDto createIndexData(IndexDataCreateRequest indexDataCreateRequest);

    byte[] getCsvData(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        String sortField,
        String sortDirection);
}
