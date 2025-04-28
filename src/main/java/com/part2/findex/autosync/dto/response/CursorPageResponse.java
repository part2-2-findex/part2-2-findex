package com.part2.findex.autosync.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
    private List<T> content;
    private String nextCursor;
    private String nextIdAfter;
    private Integer size;
    private Long totalElements;
    private boolean hasNext;
}