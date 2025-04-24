package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import com.part2.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;

public interface IndexInfoService {
    PageResponse<IndexInfoDto> findAllBySearchItem(IndexSearchRequest indexSearchRequest);

    IndexInfoDto create(IndexInfoCreateRequest indexInfoCreateRequest);

    IndexInfoDto update(Long id, IndexInfoUpdateRequest indexInfoUpdateRequest);

}
