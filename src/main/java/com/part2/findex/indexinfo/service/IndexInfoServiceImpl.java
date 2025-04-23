package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.mapper.IndexInfoMapper;
import com.part2.findex.indexinfo.mapper.PageResponseMapper;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexInfoServiceImpl implements IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final PageResponseMapper pageResponseMapper;
    private final IndexInfoMapper indexInfoMapper;

    @Override
    public PageResponse<IndexInfoDto> findAllBySearchItem(IndexSearchRequest indexSearchRequest) {
        String indexClassification = makeLikeParam(indexSearchRequest.getIndexClassification());
        String indexName = makeLikeParam(indexSearchRequest.getIndexName());

        Page<IndexInfoDto> indexInfos = indexInfoRepository
                .findAllBySearchItem(indexClassification, indexName, indexSearchRequest.getFavorite(), toPageable(indexSearchRequest))
                .map(indexInfoMapper::toDto);

        return pageResponseMapper.fromPage(indexInfos);
    }

    private Pageable toPageable(IndexSearchRequest request) {
        String sortField = request.getSortField() != null ? request.getSortField() : "indexClassification";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "asc";
        int size = request.getSize() != null ? request.getSize() : 10;
        int page = 0;

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortField).descending() :
                Sort.by(sortField).ascending();

        return PageRequest.of(page, size, sort);
    }

    private String makeLikeParam(String value) {
        if (value == null || value.isBlank()) {
            return "%";
        }
        return "%" + value + "%";
    }
}
