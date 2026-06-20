package com.example.asset_tracker_backend.pricing.provider;

import com.example.asset_tracker_backend.pricing.dto.CoinDto;
import com.example.asset_tracker_backend.pricing.dto.CurrencyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CurrencyConverter {
    private final RestClient restClient;
    String toCurrency = "MYR";

    public CurrencyConverter(@Value("${pricing.currency.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Convert an amount from one currency to another.
     * @param fromCurrency source currency code (e.g. "USD")
     * @return converted amount, or null if conversion failed
     */
    public Double convert(String fromCurrency) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("base", fromCurrency)
                            .queryParam("quotes", toCurrency)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CurrencyDto>>() {});

            log.info("Converted {} to {} {}",
                    fromCurrency, response, toCurrency);


            return response != null ? response.get(0).getRate() : (double) 0;
        } catch (Exception e) {
            log.error("Failed to convert currency: {}", e.getMessage());
            return null;
        }
    }
}
