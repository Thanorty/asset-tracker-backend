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
public class StockPriceService {

    private final RestClient restClient;

    public StockPriceService(@Value("${pricing.yahoo.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
    }

    /**
     * Search for stocks by query string.
     */
    public List<StockSearchResult> searchStocks(String query) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/finance/search")
                            .queryParam("q", query)
                            .queryParam("quotesCount", 10)
                            .queryParam("newsCount", 0)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null || !response.containsKey("quotes")) {
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> quotes = (List<Map<String, Object>>) response.get("quotes");

            return quotes.stream()
                    .map(q -> new StockSearchResult(
                            (String) q.getOrDefault("symbol", ""),
                            (String) q.getOrDefault("shortname", q.getOrDefault("longname", "")),
                            (String) q.getOrDefault("exchange", ""),
                            (String) q.getOrDefault("quoteType", "EQUITY")
                    ))
                    .toList();

        } catch (Exception e) {
            log.error("Failed to search stocks: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get current price for a stock symbol.
     */
    public Double getPrice(String symbol) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v8/finance/chart/{symbol}")
                            .queryParam("range", "1d")
                            .queryParam("interval", "1d")
                            .build(symbol))
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
                return ((Number) price).doubleValue();
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to get stock price for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * Get prices for multiple stock symbols.
     */
    public Map<String, Double> getPrices(List<String> symbols) {
        Map<String, Double> prices = new java.util.HashMap<>();
        for (String symbol : symbols) {
            try {
                Double price = getPrice(symbol);
                if (price != null) {
                    prices.put(symbol, price);
                }
                // Small delay to avoid rate limiting
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return prices;
    }
}

