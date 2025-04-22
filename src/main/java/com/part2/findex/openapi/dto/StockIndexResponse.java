package com.part2.findex.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockIndexResponse(
    Response response
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Response(
      Header header,
      Body body
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Header(
      String resultCode,    // 결과 코드
      String resultMsg      // 결과 메세지
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Body(
      int numOfRows,      // 한 페이지 결과 수
      int pageNo,         // 페이지 번호
      int totalCount,     // 전체 결과 수
      Items items
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Items(
      @JsonProperty("item")     // 중첩 객체 구조 + 제네릭 리스트의 경우 Jackson이 자동으로 매핑하지 못할 수 있어 작성
      List<StockItem> item
  ) {}
}
