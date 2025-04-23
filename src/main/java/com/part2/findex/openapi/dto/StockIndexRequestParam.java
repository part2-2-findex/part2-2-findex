package com.part2.findex.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StockIndexRequestParam {
  private int numOfRows;      // 한 페이지 결과 수
  private int pageNo;         // 페이지 번호

  @Default
  private String resultType = "json";
  private String serviceKey;    // 유일한 필수, decoding 키를 넣어야함.

  private String basDt;           // 기준일자, 검색값과 기준일자가 일치하는 데이터를 검색
  private String beginBasDt;      // 기준일자, 기준일자가 검색값보다 크거나 같은 데이터를 검색
  private String endBasDt;        // 기준일자, 기준일자가 검색값보다 작은 데이터를 검색
  private String likeBasDt;       // 기준일자, 기준일자값이 검색값을 포함하는 데이터를 검색
  private String idxNm;           // 지수명, 검색값과 지수명이 일치하는 데이터를 검색
  private String likeIdxNm;       // 지수명, 지수명이 검색값을 포함하는 데이터를 검색

  private Long beginEpyItmsCnt;   // 채용종목 수, 채용종목 수가 검색값보다 크거나 같은 데이터를 검색
  private Long endEpyItmsCnt;     // 채용종목 수, 채용종목 수가 검색값보다 작은 데이터를 검색
  private Double beginFltRt;      // 등락률, 등락률이 검색값보다 크거나 같은 데이터를 검색
  private Double endFltRt;        // 등락률, 등락률이 검색값보다 작은 데이터를 검색

  private Long beginTrqu;         // 거래량, 거래량이 검색값보다 크거나 같은 데이터를 검색
  private Long endTrqu;           // 거래량, 거래량이 검색값보다 작은 데이터를 검색
  private Long beginTrPrc;        // 거래대금, 거래대금이 검색값보다 크거나 같은 데이터를 검색
  private Long endTrPrc;          // 거래대금, 거래대금이 검색값보다 작은 데이터를 검색

  private Long beginLstgMrktTotAmt;     // 상장시가총액, 상장시가총액이 검색값보다 크거나 같은 데이터를 검색
  private Long endLstgMrktTotAmt;       // 상장시가총액, 상장시가총액이 검색값보다 작은 데이터를 검색
  private Double beginLsYrEdVsFltRg;    // 전년말대비_등락폭, 전년말대비_등락폭이 검색값보다 크거나 같은 데이터를 검색
  private Double endLsYrEdVsFltRg;      // 전년말대비_등락폭, 전년말대비_등락폭이 검색값보다 작은 데이터를 검색
  private Double beginLsYrEdVsFltRt;    // 전년말대비_등락률, 전년말대비_등락률이 검색값보다 크거나 같은 데이터를 검색
  private Double endLsYrEdVsFltRt;      // 전년말대비_등락률, 전년말대비_등락률이 검색값보다 작은 데이터를 검색
}