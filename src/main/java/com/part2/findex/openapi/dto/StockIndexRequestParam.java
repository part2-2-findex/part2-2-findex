package com.part2.findex.openapi.dto;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class StockIndexRequestParam {
  private int numOfRows;
  private int pageNo;
  @Default
  private String resultType = "json";
  private String serviceKey;    // 필수

  private String basDt;
  private String beginBasDt;
  private String endBasDt;
  private String likeBasDt;
  private String idxNm;
  private String likeIdxNm;

  private Long beginEpyItmsCnt;
  private Long endEpyItmsCnt;
  private Double beginFltRt;
  private Double endFltRt;

  private Long beginTrqu;
  private Long endTrqu;
  private Long beginTrPrc;
  private Long endTrPrc;

  private Long beginLstgMrktTotAmt;
  private Long endLstgMrktTotAmt;
  private Double beginLsYrEdVsFltRg;
  private Double endLsYrEdVsFltRg;
  private Double beginLsYrEdVsFltRt;
  private Double endLsYrEdVsFltRt;
}