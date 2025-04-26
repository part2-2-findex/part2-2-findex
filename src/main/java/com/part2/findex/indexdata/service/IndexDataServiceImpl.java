package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.CsvExportDto;
import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.mapper.IndexDataResponseMapper;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexdata.repository.querydsl.IndexDataQueryRepository;
import com.part2.findex.indexdata.util.Csv;
import java.time.LocalDate;
import java.util.List;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.indexdata.service.strategy.SortStrategyContext;
import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataQueryRepository indexDataQueryRepository;
    private final SortStrategyContext sortStrategyContext;
    private final IndexDataResponseMapper indexDataResponseMapper;
    private final Csv csvUtil;          // csvExport 관련 util 클래스

    @Transactional
    @Override
    public IndexDataDto createIndexData(IndexDataCreateRequest indexDataCreateRequest) {
        IndexInfo indexInfo = indexInfoRepository.findById(indexDataCreateRequest.indexId())
                .orElseThrow(() -> new NoSuchElementException("Index info not found"));

        IndexData indexData = IndexData.builder()
                .indexInfo(indexInfo)
                .baseDate(indexDataCreateRequest.baseDate())
                .sourceType(indexDataCreateRequest.sourceType())
                .marketPrice(indexDataCreateRequest.marketPrice())
                .closingPrice(indexDataCreateRequest.closingPrice())
                .highPrice(indexDataCreateRequest.highPrice())
                .lowPrice(indexDataCreateRequest.lowPrice())
                .versus(indexDataCreateRequest.versus())
                .fluctuationRate(indexDataCreateRequest.fluctuationRate())
                .tradingQuantity(indexDataCreateRequest.tradingQuantity())
                .tradingPrice(indexDataCreateRequest.tradingPrice())
                .marketTotalAmount(indexDataCreateRequest.marketTotalAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        indexDataRepository.save(indexData);
        return new IndexDataDto(
                indexData.getId(),
                indexData.getIndexInfo().getId(),
                indexData.getBaseDate(),
                indexData.getSourceType(),
                indexData.getMarketPrice(),
                indexData.getClosingPrice(),
                indexData.getHighPrice(),
                indexData.getLowPrice(),
                indexData.getVersus(),
                indexData.getFluctuationRate(),
                indexData.getTradingQuantity(),
                indexData.getTradingPrice(),
                indexData.getMarketTotalAmount()
        );
    }

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

    @Transactional
    @Override
    public byte[] getCsvData(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        String sortField,
        String sortDirection
    ) {
        List<CsvExportDto> data = indexDataQueryRepository.getCsvData(indexInfoId, startDate, endDate, sortField, sortDirection);
        System.out.println("Csv Data 사이즈: " + data.size());     ///
        System.out.println(data);   ///
        byte[] csvBytes;
        try {
            csvBytes = csvUtil.generateCsvWithOpenCsv(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return csvBytes;
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
