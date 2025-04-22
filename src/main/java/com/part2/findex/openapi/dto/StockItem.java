package com.part2.findex.openapi.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockItem(
    String basDt,
    String idxNm,
    String idxCsf,
    int epyItmsCnt,
    double clpr,
    double vs,
    double fltRt,
    double mkp,
    double hipr,
    double lopr,
    long trqu,
    long trPrc,
    long lstgMrktTotAmt,
    double lsYrEdVsFltRg,
    double lsYrEdVsFltRt,
    double yrWRcrdHgst,
    String yrWRcrdHgstDt,
    double yrWRcrdLwst,
    String yrWRcrdLwstDt,
    String basPntm,
    double basIdx
) {}
