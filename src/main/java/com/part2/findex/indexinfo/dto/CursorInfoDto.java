package com.part2.findex.indexinfo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CursorInfoDto {
    private final String fieldCursor;
    private final Long idCursor;
}
