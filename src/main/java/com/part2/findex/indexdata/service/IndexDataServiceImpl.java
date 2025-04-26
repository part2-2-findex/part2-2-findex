package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.mapper.IndexDataResponseMapper;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexdata.service.strategy.SortStrategyContext;
import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final SortStrategyContext sortStrategyContext;
    private final IndexDataResponseMapper indexDataResponseMapper;

    @Override
    public PageResponse<IndexDataResponse> findAllBySearchItem(IndexDataRequest indexDataRequest) {
        CursorInfoDto cursorInfo = prepareCursor(indexDataRequest);

        List<IndexData> result = sortStrategyContext.findAllBySearch(indexDataRequest, cursorInfo);

        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = !result.isEmpty() && result.size() >= indexDataRequest.getSize();

        if (hasNext) {
            IndexData lastItem = result.get(result.size() - 1);
            String fieldCursor = null;

            if ("baseDate".equals(indexDataRequest.getSortField())) {
                fieldCursor = lastItem.getBaseDate().toString();  // BaseDate 기준으로 커서 설정
            } else if ("closingPrice".equals(indexDataRequest.getSortField())) {
                fieldCursor = lastItem.getClosingPrice().toString();  // ClosingPrice 기준으로 커서 설정
            }

            nextIdAfter = lastItem.getId();
            nextCursor = fieldCursor + "|" + nextIdAfter;  // 인코딩 제거
        }

        LocalDate startDate = (indexDataRequest.getStartDate() != null) ? LocalDate.parse(indexDataRequest.getStartDate()) : LocalDate.of(1900, 1, 1);
        LocalDate endDate = (indexDataRequest.getEndDate() != null) ? LocalDate.parse(indexDataRequest.getEndDate()) : LocalDate.now();

        Long totalSize = indexDataRepository.countAllByFilters(
                indexDataRequest.getIndexInfoId(),
                startDate,
                endDate
        );

        List<IndexDataResponse> content = result.stream()
                .map(indexDataResponseMapper::toDto)
                .toList();

        return PageResponse.<IndexDataResponse>builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .size(indexDataRequest.getSize())
                .totalElements(totalSize)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    @Override
    public void updateIndexData(Long indexDataId, IndexDataUpdateRequest indexDataUpdateRequest) {
        IndexData indexData = indexDataRepository.findById(indexDataId)
                .orElseThrow(() -> new NoSuchElementException("Index data not found"));

        indexData.update(indexDataUpdateRequest);

    }

    @Transactional
    @Override
    public void deleteIndexData(Long indexDataId) {
        indexDataRepository.deleteById(indexDataId);
    }

    private CursorInfoDto prepareCursor(IndexDataRequest request) {
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
