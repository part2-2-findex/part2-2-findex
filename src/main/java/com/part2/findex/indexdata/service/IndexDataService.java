package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexinfo.dto.response.PageResponse;

public interface IndexDataService {

    PageResponse<IndexDataResponse> findAllBySearchItem(IndexDataRequest indexSearchRequest);

    void updateIndexData(Long indexDataId, IndexDataUpdateRequest indexDataUpdateRequest);

    void deleteIndexData(Long indexDataId);
}
