package com.part2.findex.indexinfo.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        boolean hasNext,
        Long totalElements
) {
}
