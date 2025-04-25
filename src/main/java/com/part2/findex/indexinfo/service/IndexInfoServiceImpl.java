package com.part2.findex.indexinfo.service;

import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import com.part2.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.mapper.IndexInfoMapper;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.indexinfo.service.strategy.SortStrategyContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IndexInfoServiceImpl implements IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;
    private final SortStrategyContext sortStrategyContext;

    @Override
    public PageResponse<IndexInfoDto> findAllBySearchItem(IndexSearchRequest indexSearchRequest) {
        // 1. 커서 파싱 (ID 기준)
        Long cursorValue = parseCursor(indexSearchRequest.getCursor());

        CursorInfoDto cursorInfo =  prepareCursor(indexSearchRequest, cursorValue);

        // 2. 데이터 조회 (정렬 없음)
        List<IndexInfo> result = sortStrategyContext.findAllBySearch(indexSearchRequest, cursorInfo);

        // 3. 다음 커서 계산 (ID 기준)
        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = !result.isEmpty() && result.size() >= indexSearchRequest.getSize();
        if (hasNext) {
            IndexInfo lastItem = result.get(result.size() - 1);
            nextIdAfter = lastItem.getId();  // 여기서 nextIdAfter 설정
            nextCursor = encodeCursor(nextIdAfter); // 필요하면 커서도 세팅
        }

        // 3. 전체 개수 조회
        Long totalSize = indexInfoRepository.countAllByFilters(
                indexSearchRequest.getIndexClassification(),
                indexSearchRequest.getIndexName(),
                indexSearchRequest.getFavorite()
        );


        // 4. DTO 변환
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
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found") );
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

        double employedItemsCount = indexInfoUpdateRequest.getEmployedItemsCount() == 0 ? indexInfo.getEmployedItemsCount() : indexInfoUpdateRequest.getEmployedItemsCount();
        String basePointInTime = indexInfoUpdateRequest.getBasePointInTime() == null ? indexInfo.getBasePointInTime() : indexInfoUpdateRequest.getBasePointInTime().toString();
        double baseIndex = indexInfoUpdateRequest.getBaseIndex() == 0 ? indexInfo.getBaseIndex() : indexInfoUpdateRequest.getBaseIndex();
        boolean favorite = indexInfoUpdateRequest.getFavorite() == null ? indexInfo.isFavorite() : indexInfoUpdateRequest.getFavorite();


        indexInfo.update(employedItemsCount, basePointInTime, baseIndex, favorite);

        return indexInfoMapper.toDto(indexInfoRepository.save(indexInfo));
    }

    @Override
    public void delete(Long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("IndexInfo with id " + id + " not found") );

        indexInfoRepository.deleteById(id);
    }

    private Long parseCursor(String cursor) {
        if (cursor == null) return null;
        try {
            return Long.parseLong(new String(Base64.getDecoder().decode(cursor)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor");
        }
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }

    private CursorInfoDto prepareCursor(IndexSearchRequest request, Long cursorValue) {
        String sortField = request.getSortField();
        String fieldCursor = null;
        Long idCursor = null;

        if (cursorValue != null) {
            IndexInfo lastItem = indexInfoRepository.findById(cursorValue).orElse(null);
            if (lastItem != null) {
                if ("indexClassification".equals(sortField)) {
                    fieldCursor = lastItem.getIndexClassification();
                } else if ("indexName".equals(sortField)) {
                    fieldCursor = lastItem.getIndexName();
                }
                idCursor = lastItem.getId();
            }
        }

        return new CursorInfoDto(fieldCursor, idCursor);
    }
}
