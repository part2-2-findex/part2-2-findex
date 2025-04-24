package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import com.part2.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.mapper.IndexInfoMapper;
import com.part2.findex.indexinfo.mapper.PageResponseMapper;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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
                .findAllBySearchItem(indexClassification, indexName, indexSearchRequest.getFavorite(),toPageable(indexSearchRequest))
                .map(indexInfoMapper::toDto);

        return pageResponseMapper.fromPage(indexInfos);
    }

    @Override
    @Transactional
    public IndexInfoDto create(IndexInfoCreateRequest indexInfoCreateRequest) {

        IndexInfo indexInfo = indexInfoRepository.save(
                new IndexInfo(
                        indexInfoCreateRequest.getIndexClassification(),
                        indexInfoCreateRequest.getIndexName(),
                        indexInfoCreateRequest.getEmployedItemsCount(),
                        indexInfoCreateRequest.getBasePointInTime().toString(),
                        indexInfoCreateRequest.getBaseIndex(),
                        indexInfoCreateRequest.getFavorite()));

        return indexInfoMapper.toDto(indexInfo);
    }

    @Override
    @Transactional
    public IndexInfoDto update(Long id, IndexInfoUpdateRequest indexInfoUpdateRequest) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found") );

        int employedItemsCount = indexInfoUpdateRequest.getEmployedItemsCount() == 0 ? indexInfo.getEmployedItemsCount() : indexInfoUpdateRequest.getEmployedItemsCount();
        String basePointInTime = indexInfoUpdateRequest.getBasePointInTime() == null ? indexInfo.getBasePointInTime() : indexInfoUpdateRequest.getBasePointInTime().toString();
        double baseIndex = indexInfoUpdateRequest.getBaseIndex() == 0 ? indexInfo.getBaseIndex() : indexInfoUpdateRequest.getBaseIndex();
        boolean favorite = indexInfoUpdateRequest.getFavorite() == null ? indexInfo.isFavorite() : indexInfoUpdateRequest.getFavorite();


        indexInfo.update(employedItemsCount, basePointInTime, baseIndex, favorite);

        return indexInfoMapper.toDto(indexInfoRepository.save(indexInfo));
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
