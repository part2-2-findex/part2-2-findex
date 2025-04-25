package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;

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
}
