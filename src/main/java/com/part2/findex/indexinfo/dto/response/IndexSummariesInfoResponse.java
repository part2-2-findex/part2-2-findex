package com.part2.findex.indexinfo.dto.response;

import lombok.Builder;

@Builder
public record IndexSummariesInfoResponse(
        Long id,
        String indexClassification,
        String indexName
) {
}
