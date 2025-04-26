package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import com.part2.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.IndexSummariesInfoResponse;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.indexinfo.mapper.IndexInfoMapper;
import com.part2.findex.indexinfo.mapper.IndexSummariesInfoMapper;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.indexinfo.service.strategy.SortStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IndexInfoServiceImpl implements IndexInfoService {

    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;
    private final SortStrategyContext sortStrategyContext;
    private final IndexSummariesInfoMapper indexSummariesInfoMapper;

    @Override
    public List<IndexSummariesInfoResponse> findAllBySummeriesItem() {
        return indexInfoRepository.findAll().stream().map(indexSummariesInfoMapper::toDto).toList();
    }

    @Override
    public PageResponse<IndexInfoDto> findAllBySearchItem(IndexSearchRequest indexSearchRequest) {
        CursorInfoDto cursorInfo = prepareCursor(indexSearchRequest);

        List<IndexInfo> result = sortStrategyContext.findAllBySearch(indexSearchRequest, cursorInfo);

        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = !result.isEmpty() && result.size() >= indexSearchRequest.getSize();

        if (hasNext) {
            IndexInfo lastItem = result.get(result.size() - 1);
            String fieldCursor = null;

            if ("indexClassification".equals(indexSearchRequest.getSortField())) {
                fieldCursor = lastItem.getIndexClassification();
            } else if ("indexName".equals(indexSearchRequest.getSortField())) {
                fieldCursor = lastItem.getIndexName();
            }

            nextIdAfter = lastItem.getId();
            nextCursor = fieldCursor + "|" + nextIdAfter;  // 인코딩 제거
        }

        Long totalSize = indexInfoRepository.countAllByFilters(
                prepareLikeParam(indexSearchRequest.getIndexClassification()),
                prepareLikeParam(indexSearchRequest.getIndexName()),
                indexSearchRequest.getFavorite()
        );

        List<IndexInfoDto> content = result.stream()
                .map(indexInfoMapper::toDto)
                .toList();

        return PageResponse.<IndexInfoDto>builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .size(indexSearchRequest.getSize())
                .totalElements(totalSize)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public IndexInfoDto findById(Long id) {
        return indexInfoRepository.findById(id)
                .map(indexInfoMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found"));
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
                        indexInfoCreateRequest.getFavorite(),
                        SourceType.사용자));

        return indexInfoMapper.toDto(indexInfo);
    }

    @Override
    @Transactional
    public IndexInfoDto update(Long id, IndexInfoUpdateRequest indexInfoUpdateRequest) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found"));

        double employedItemsCount = indexInfoUpdateRequest.getEmployedItemsCount() == 0 ? indexInfo.getEmployedItemsCount() : indexInfoUpdateRequest.getEmployedItemsCount();
        String basePointInTime = indexInfoUpdateRequest.getBasePointInTime() == null ? indexInfo.getBasePointInTime() : indexInfoUpdateRequest.getBasePointInTime().toString();
        double baseIndex = indexInfoUpdateRequest.getBaseIndex() == 0 ? indexInfo.getBaseIndex() : indexInfoUpdateRequest.getBaseIndex();
        boolean favorite = indexInfoUpdateRequest.getFavorite() == null ? indexInfo.isFavorite() : indexInfoUpdateRequest.getFavorite();


        indexInfo.update(employedItemsCount, basePointInTime, baseIndex, favorite, BASE_INDEX_TOLERANCE);

        return indexInfoMapper.toDto(indexInfoRepository.save(indexInfo));
    }

    @Override
    public void delete(Long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found"));

        indexInfoRepository.deleteById(id);
    }

    private CursorInfoDto prepareCursor(IndexSearchRequest request) {
        CursorInfoDto cursorInfoDto = new CursorInfoDto();
        String cursor = request.getCursor();

        if (cursor != null && !cursor.isEmpty()) {
            String[] parts = cursor.split("\\|");
            cursorInfoDto.setFieldCursor(parts[0]);
            cursorInfoDto.setIdCursor(Long.parseLong(parts[1]));
        } else {
            cursorInfoDto.setFieldCursor(null);
            cursorInfoDto.setIdCursor(null);
        }

        return cursorInfoDto;
    }

    private String prepareLikeParam(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "%";  // 모든 값 포함
        }
        return "%" + param.trim() + "%";
    }
}
