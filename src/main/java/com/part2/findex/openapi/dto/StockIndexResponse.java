package com.part2.findex.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockIndexResponse (
    Header header,
    Body body
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Header (
      String resultCode,
      String resultMsg
  ){}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Body(
      int numOfRows,
      int pageNo,
      int totalCount,
      Items items
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Items(
      @JsonProperty("item") List<StockItem> item
  ) {}
}