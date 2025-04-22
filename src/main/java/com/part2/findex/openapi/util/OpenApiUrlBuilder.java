package com.part2.findex.openapi.util;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import java.net.URI;
import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriComponentsBuilder;

@UtilityClass
public final class OpenApiUrlBuilder {
  private static final String BASE_URL = "https://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex";

  public static String buildUrl(StockIndexRequestParam param) {
    URI uri = URI.create(BASE_URL);   // URI.create() 메서드가 유효한 URI 인지 검증
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance().uri(uri)
        .queryParam("serviceKey", param.getServiceKey())
        .queryParam("resultType", param.getResultType())
        .queryParam("pageNo", param.getPageNo())
        .queryParam("numOfRows", param.getNumOfRows());

    if (param.getBasDt() != null) builder.queryParam("basDt", param.getBasDt());
    if (param.getBeginBasDt() != null) builder.queryParam("beginBasDt", param.getBeginBasDt());
    if (param.getEndBasDt() != null) builder.queryParam("endBasDt", param.getEndBasDt());
    if (param.getLikeBasDt() != null) builder.queryParam("likeBasDt", param.getLikeBasDt());
    if (param.getIdxNm() != null) builder.queryParam("idxNm", param.getIdxNm());
    if (param.getLikeIdxNm() != null) builder.queryParam("likeIdxNm", param.getLikeIdxNm());
    if (param.getBeginEpyItmsCnt() != null) builder.queryParam("beginEpyItmsCnt", param.getBeginEpyItmsCnt());
    if (param.getEndEpyItmsCnt() != null) builder.queryParam("endEpyItmsCnt", param.getEndEpyItmsCnt());
    if (param.getBeginFltRt() != null) builder.queryParam("beginFltRt", param.getBeginFltRt());
    if (param.getEndFltRt() != null) builder.queryParam("endFltRt", param.getEndFltRt());
    if (param.getBeginTrqu() != null) builder.queryParam("beginTrqu", param.getBeginTrqu());
    if (param.getEndTrqu() != null) builder.queryParam("endTrqu", param.getEndTrqu());
    if (param.getBeginTrPrc() != null) builder.queryParam("beginTrPrc", param.getBeginTrPrc());
    if (param.getEndTrPrc() != null) builder.queryParam("endTrPrc", param.getEndTrPrc());
    if (param.getBeginLstgMrktTotAmt() != null) builder.queryParam("beginLstgMrktTotAmt", param.getBeginLstgMrktTotAmt());
    if (param.getEndLstgMrktTotAmt() != null) builder.queryParam("endLstgMrktTotAmt", param.getEndLstgMrktTotAmt());
    if (param.getBeginLsYrEdVsFltRg() != null) builder.queryParam("beginLsYrEdVsFltRg", param.getBeginLsYrEdVsFltRg());
    if (param.getEndLsYrEdVsFltRg() != null) builder.queryParam("endLsYrEdVsFltRg", param.getEndLsYrEdVsFltRg());
    if (param.getBeginLsYrEdVsFltRt() != null) builder.queryParam("beginLsYrEdVsFltRt", param.getBeginLsYrEdVsFltRt());
    if (param.getEndLsYrEdVsFltRt() != null) builder.queryParam("endLsYrEdVsFltRt", param.getEndLsYrEdVsFltRt());

    return builder.toUriString();
  }
}