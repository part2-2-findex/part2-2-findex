package com.part2.findex.indexinfo.dto.response;

import lombok.Builder;

@Builder
public record IndexInfoDto(
    Long id,
    String indexClassification,
    String indexName,
    Integer employedItemsCount,
    String basePointInTime,
    Double baseIndex,
    String sourceType,
    Boolean favorite) {
}
