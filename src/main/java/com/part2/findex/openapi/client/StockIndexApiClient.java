package com.part2.findex.openapi.client;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.openapi.util.OpenApiUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class StockIndexApiClient {

    private final RestTemplate restTemplate;

    public StockIndexResponse fetchStockIndices(StockIndexRequestParam param) {
        String url = OpenApiUrlBuilder.buildUrl(param);

        return getStockIndexResponse(url);
    }

    private StockIndexResponse getStockIndexResponse(String url) {
        try {
            URI uri = new URI(url);
            return restTemplate.getForObject(uri, StockIndexResponse.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("잘못된 URI 형식입니다: " + url, e);
        }catch (RestClientException e){
            throw new RuntimeException("API 호출 중 문제가 발생했습니다: " + e.getMessage(), e);
        }
    }
}