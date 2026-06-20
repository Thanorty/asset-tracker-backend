package com.example.asset_tracker_backend.pricing.provider;

import com.example.asset_tracker_backend.pricing.dto.StockSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoldService {

    private final RestClient restClient;

    public GoldService(@Value("${pricing.yahoo.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
    }

    /**
     * Get current price for a stock symbol.
     */
    public Double getPrice() {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v8/finance/chart/GC=F")
                            .queryParam("range", "1d")
                            .queryParam("interval", "1d")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null) return null;

            @SuppressWarnings("unchecked")
            var chart = (Map<String, Object>) response.get("chart");
            @SuppressWarnings("unchecked")
            var results = (List<Map<String, Object>>) chart.get("result");

            if (results == null || results.isEmpty()) return null;

            @SuppressWarnings("unchecked")
            var meta = (Map<String, Object>) results.get(0).get("meta");
            var price = meta.get("regularMarketPrice");

            if (price instanceof Number) {
                return (((Number) price).doubleValue())/31.1035;
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to get gold price {}", e.getMessage());
            return null;
        }
    }
}

