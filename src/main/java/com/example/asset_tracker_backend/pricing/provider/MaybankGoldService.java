package com.example.asset_tracker_backend.pricing.provider;

import com.example.asset_tracker_backend.pricing.dto.GoldPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MaybankGoldService {

    @Value("${pricing.maybank-gold.url}")
    private String maybankGoldUrl;

    /**
     * Scrape Maybank gold selling price (MYR per gram).
     * Cached for 5 minutes.
     */
    @Cacheable(value = "goldPrice", unless = "#result == null")
    public GoldPriceDto getGoldPrice() {
        try {
            Document doc = Jsoup.connect(maybankGoldUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Try to find gold price from the page
            // Maybank gold page typically shows Selling and Buying prices
            Double sellPrice = null;
            Double buyPrice = null;

            // Look for table rows containing gold prices
            for (Element row : doc.select("table tr, .gold-rate, .rate-table tr")) {
                String text = row.text().toLowerCase();
                boolean isGoldRow = text.contains("below 100 grams") ;
                if (isGoldRow) {
                    List<Double> goldPrice = extractPrice(row.text());
                    sellPrice = goldPrice.get(1);
                    buyPrice = goldPrice.get(0);
                }
            }

            if (sellPrice != null) {
                log.info("Maybank gold sell price: MYR {}/gram", sellPrice);
                return new GoldPriceDto(sellPrice, buyPrice, "MYR", "gram");
            }

            log.warn("Could not parse Maybank gold price from page");
            return null;

        } catch (Exception e) {
            log.error("Failed to fetch Maybank gold price: {}", e.getMessage());
            return null;
        }
    }

    private List<Double> extractPrice(String text) {
        try {
            // Extract numeric value like "234.56" from text
            var matcher = java.util.regex.Pattern.compile("(\\d+\\.\\d{2})").matcher(text);
            List<Double> values = new ArrayList<>();

            while (matcher.find()) {
                values.add(Double.parseDouble(matcher.group()));
            }

            return values;

        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }
}

