package com.part2.findex.indexinfo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexSearchRequest {
    private String indexClassification; // 지수 분류명
    private String indexName;           // 지수명
    private Boolean favorite;           // 즐겨찾기 여부

    private Long idAfter;               // 이전 페이지 마지막 요소 ID
    private String cursor;              // 커서(다음 페이지 시작점)

    @Builder.Default
    private String sortField = "indexClassification"; // 정렬 필드

    @Builder.Default
    @Pattern(regexp = "asc|desc")
    private String sortDirection = "asc";             // 정렬 방향

    @Min(value = 1, message = "{Min}")
    @Builder.Default
    private Integer size = 10;                        // 페이지 크기

    @Override
    public String toString() {
        return "IndexSearchRequest{" +
                "indexClassification='" + indexClassification + '\'' +
                ", indexName='" + indexName + '\'' +
                ", favorite=" + favorite +
                ", idAfter=" + idAfter +
                ", cursor='" + cursor + '\'' +
                ", sortField='" + sortField + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", size=" + size +
                '}';
    }
}
