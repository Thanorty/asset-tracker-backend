package com.example.asset_tracker_backend.pricing.service;

import com.example.asset_tracker_backend.portfolio.model.Asset;
import com.example.asset_tracker_backend.portfolio.repository.AssetRepository;
import com.example.asset_tracker_backend.pricing.dto.GoldPriceDto;
import com.example.asset_tracker_backend.pricing.dto.PriceUpdateResult;
import com.example.asset_tracker_backend.pricing.provider.CoinGeckoService;
import com.example.asset_tracker_backend.pricing.provider.MaybankGoldService;
import com.example.asset_tracker_backend.pricing.provider.StockPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final AssetRepository assetRepository;
    private final CoinGeckoService coinGeckoService;
    private final StockPriceService stockPriceService;
    private final MaybankGoldService maybankGoldService;

    /**
     * Update prices for all assets in the database.
     */
    public PriceUpdateResult updateAllAssetPrices() {
        List<Asset> allAssets = assetRepository.findAll();
        int updated = 0;
        int failed = 0;

        // Group by type
        Map<String, List<Asset>> byType = allAssets.stream()
                .collect(Collectors.groupingBy(a -> a.getType() != null ? a.getType().toUpperCase() : "UNKNOWN"));

        // Update crypto prices (batch via CoinGecko)
        List<Asset> cryptoAssets = byType.getOrDefault("CRYPTO", List.of());
        if (!cryptoAssets.isEmpty()) {
            List<String> coinIds = cryptoAssets.stream()
                    .map(Asset::getExternalId)
                    .filter(id -> id != null && !id.isBlank())
                    .toList();

            if (!coinIds.isEmpty()) {
                Map<String, Double> prices = coinGeckoService.getPrices(coinIds, "usd");
                for (Asset asset : cryptoAssets) {
                    Double price = prices.get(asset.getExternalId());
                    if (price != null) {
                        asset.setUnitPrice(price);
                        asset.setCurrentValue(price * (asset.getQuantity() != null ? asset.getQuantity() : 0));
                        asset.setCurrency("USD");
                        asset.setLastPriceUpdate(LocalDateTime.now());
                        updated++;
                    } else {
                        failed++;
                    }
                }
            }
        }

        // Update stock prices (individual via Yahoo Finance)
        List<Asset> stockAssets = byType.getOrDefault("STOCK", List.of());
        if (!stockAssets.isEmpty()) {
            List<String> symbols = stockAssets.stream()
                    .map(Asset::getSymbol)
                    .filter(s -> s != null && !s.isBlank())
                    .toList();

            if (!symbols.isEmpty()) {
                Map<String, Double> prices = stockPriceService.getPrices(symbols);
                for (Asset asset : stockAssets) {
                    Double price = prices.get(asset.getSymbol());
                    if (price != null) {
                        asset.setUnitPrice(price);
                        asset.setCurrentValue(price * (asset.getQuantity() != null ? asset.getQuantity() : 0));
                        asset.setCurrency("USD");
                        asset.setLastPriceUpdate(LocalDateTime.now());
                        updated++;
                    } else {
                        failed++;
                    }
                }
            }
        }

        // Update gold prices (Maybank)
        List<Asset> goldAssets = byType.getOrDefault("GOLD", List.of());
        if (!goldAssets.isEmpty()) {
            GoldPriceDto goldPrice = maybankGoldService.getGoldPrice();
            if (goldPrice != null && goldPrice.getSellPrice() != null) {
                for (Asset asset : goldAssets) {
                    asset.setUnitPrice(goldPrice.getSellPrice());
                    asset.setCurrentValue(goldPrice.getSellPrice() * (asset.getQuantity() != null ? asset.getQuantity() : 0));
                    asset.setCurrency("MYR");
                    asset.setLastPriceUpdate(LocalDateTime.now());
                    updated++;
                }
            } else {
                failed += goldAssets.size();
            }
        }

        // Save all updates
        assetRepository.saveAll(allAssets);

        String msg = String.format("Price update complete: %d updated, %d failed", updated, failed);
        log.info(msg);
        return new PriceUpdateResult(updated, failed, msg);
    }
}

