package com.part2.findex.indexinfo.mapper;

import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.stereotype.Component;

@Component
public class IndexInfoMapper {

    public IndexInfoDto toDto(IndexInfo indexInfo) {
        return IndexInfoDto.builder()
                .id(indexInfo.getId())
                .indexClassification(indexInfo.getIndexClassification())
                .indexName(indexInfo.getIndexName())
                .employedItemsCount(indexInfo.getEmployedItemsCount())
                .basePointInTime(indexInfo.getBasePointInTime())
                .baseIndex(indexInfo.getBaseIndex())
                .sourceType(indexInfo.getSourceType().toString())
                .favorite(indexInfo.isFavorite())
                .build();
    }
}
