package com.example.asset_tracker_backend.pricing.scheduler;

import com.example.asset_tracker_backend.pricing.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceScheduler {

    private final PriceService priceService;

    /**
     * Runs every 5 minutes (configurable via pricing.update-interval-ms).
     * Updates all asset prices from external APIs.
     */
//    @Scheduled(fixedRateString = "${pricing.update-interval-ms:300000}")
    public void updatePrices() {
        log.info("Scheduled price update started...");
        try {
            priceService.updateAllAssetPrices();
        } catch (Exception e) {
            log.error("Scheduled price update failed: {}", e.getMessage());
        }
    }
}

