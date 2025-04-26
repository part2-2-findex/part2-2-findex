package com.part2.findex.indexdata.mapper;

import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexdata.entity.IndexData;
import org.springframework.stereotype.Component;

@Component
public class IndexDataResponseMapper {
    public IndexDataResponse toDto(IndexData indexData){
        return IndexDataResponse.builder()
                .id(indexData.getId())
                .indexInfoId(indexData.getIndexInfo().getId())
                .baseDate(indexData.getBaseDate())
                .sourceType(indexData.getSourceType())
                .marketPrice(indexData.getMarketPrice())
                .closingPrice(indexData.getClosingPrice())
                .highPrice(indexData.getHighPrice())
                .lowPrice(indexData.getLowPrice())
                .versus(indexData.getVersus())
                .fluctuationRate(indexData.getFluctuationRate())
                .tradingQuantity(indexData.getTradingQuantity())
                .tradingPrice(indexData.getTradingPrice())
                .build();
    }
}
