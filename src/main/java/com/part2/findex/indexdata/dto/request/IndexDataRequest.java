package com.part2.findex.indexdata.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexDataRequest {
    private Long indexInfoId;         // 지수 정보 ID
    private String startDate;         // 시작 일자 (yyyy-MM-dd 형식 기대)
    private String endDate;           // 종료 일자 (yyyy-MM-dd 형식 기대)
    private Long idAfter;             // 이전 페이지 마지막 요소 ID
    private String cursor;            // 커서 (다음 페이지 시작점)

    @Builder.Default
    private String sortField = "baseDate";       // 정렬 필드 (기본값: baseDate)

    @Builder.Default
    @Pattern(regexp = "asc|desc")
    private String sortDirection = "asc";             // 정렬 방향

    @Min(value = 1, message = "{Min}")
    @Builder.Default
    private Integer size = 10;
}
