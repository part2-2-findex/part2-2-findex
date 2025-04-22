package com.part2.findex.openapi.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockItem(
    String basDt,       // 기준 일자
    String idxNm,       // 지수명
    String idxCsf,      // 지수분류명
    int epyItmsCnt,     // 채용종목 수
    double clpr,        // 종가, 정규시장의 매매시간종료시까지 형성되는 최종가격
    double vs,          // 대비, 전일 대비 등락
    double fltRt,       // 등락률, 전일 대비 등락에 따른 비율
    double mkp,         // 시가, 정규시장의 매매시간 개시 후 형성되는 최초가격
    double hipr,        // 고가, 하루 중 지수의 최고치
    double lopr,        // 저가, 하루 중 지수의 최저치
    long trqu,          // 거래량, 지수에 포함된 종목의 거래량 총합
    long trPrc,         // 거래대금, 지수에 포함된 종목의 거래대금 총합
    long lstgMrktTotAmt,    // 상장시가총액, 지수에 포함된 종목의 시가총액
    double lsYrEdVsFltRg,   // 전년말대비_등락폭, 지수의 전년말대비 등락폭
    double lsYrEdVsFltRt,   // 전년말대비_등락률, 지수의 전년말대비 등락율
    double yrWRcrdHgst,     // 연중기록최고, 지수의 연중최고치
    String yrWRcrdHgstDt,   // 연중기록 최고 일자, 지수가 연중최고치를 기록한 날짜 (ex. 20240711)
    double yrWRcrdLwst,     // 연중기록최저, 지수의 연중최저치
    String yrWRcrdLwstDt,   // 연중기록 최저 일자, 지수가 연중최저치를 기록한 날짜 (ex. 20240801)
    String basPntm,         // 기준시점, 지수를 산출하기 위한 기준시점 (ex. 19800104)
    double basIdx           // 기준지수, 기준시점의 지수값
) {}
