package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;

import java.util.List;

public interface IndexInfoService {
    PageResponse<IndexInfoDto> findAllBySearchItem(IndexSearchRequest indexSearchRequest);
}
