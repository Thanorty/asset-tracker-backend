package com.example.asset_tracker_backend.pricing.provider;

import com.example.asset_tracker_backend.pricing.dto.CoinDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoinGeckoService {

    private final RestClient restClient;

    public CoinGeckoService(@Value("${pricing.coingecko.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Get the full list of supported coins (cached for 24h).
     */
    @Cacheable(value = "coinList", unless = "#result == null || #result.isEmpty()")
    public List<CoinDto> getCoinList() {
        try {
            var response = restClient.get()
                    .uri("/coins/list")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CoinDto>>() {});
            log.info("Fetched {} coins from CoinGecko", response != null ? response.size() : 0);
            return response != null ? response : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch coin list from CoinGecko: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get prices for multiple coins at once.
     * @param coinIds comma-separated CoinGecko IDs (e.g. "bitcoin,ethereum")
     * @param currency target currency (e.g. "usd")
     * @return map of coinId -> price
     */
    public Map<String, Double> getPrices(List<String> coinIds, String currency) {
        if (coinIds == null || coinIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String ids = String.join(",", coinIds);
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/simple/price")
                            .queryParam("ids", ids)
                            .queryParam("vs_currencies", currency)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {});

            if (response == null) return Collections.emptyMap();

            // Flatten: { "bitcoin": { "usd": 50000 } } -> { "bitcoin": 50000 }
            Map<String, Double> prices = new java.util.HashMap<>();
            response.forEach((coinId, currencyMap) -> {
                Double price = currencyMap.get(currency);
                if (price != null) {
                    prices.put(coinId, price);
                }
            });

            log.info("Fetched prices for {} crypto assets", prices.size());
            return prices;
        } catch (Exception e) {
            log.error("Failed to fetch crypto prices: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}

