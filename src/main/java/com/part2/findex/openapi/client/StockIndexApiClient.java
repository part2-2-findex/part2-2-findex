package com.part2.findex.openapi.client;

import com.part2.findex.openapi.dto.StockIndexRequestParam;
import com.part2.findex.openapi.dto.StockIndexResponse;
import com.part2.findex.openapi.util.OpenApiUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class StockIndexApiClient {

    private final RestTemplate restTemplate;

    public StockIndexResponse fetchStockIndices(StockIndexRequestParam param) {

        try {
            String url = OpenApiUrlBuilder.buildUrl(param);
            URI uri = new URI(url);

            ResponseEntity<StockIndexResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 객체 생성 중 URISyntaxException 발생", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("인코딩 에러" + e);
        }
    }
}