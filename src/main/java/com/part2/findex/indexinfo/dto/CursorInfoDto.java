package com.part2.findex.indexinfo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CursorInfoDto {
    private String fieldCursor;
    private Long idCursor;
}
