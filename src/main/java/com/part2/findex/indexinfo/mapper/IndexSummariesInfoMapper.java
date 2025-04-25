package com.part2.findex.indexinfo.mapper;

import com.part2.findex.indexinfo.dto.response.IndexSummariesInfoResponse;
import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.stereotype.Component;

@Component
public class IndexSummariesInfoMapper {
    public IndexSummariesInfoResponse toDto(IndexInfo indexInfo) {
        return IndexSummariesInfoResponse.builder()
                .id(indexInfo.getId())
                .indexClassification(indexInfo.getIndexClassification())
                .indexName(indexInfo.getIndexName())
                .build();
    }
}
