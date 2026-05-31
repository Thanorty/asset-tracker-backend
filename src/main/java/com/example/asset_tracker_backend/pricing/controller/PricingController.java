package com.example.asset_tracker_backend.pricing.controller;

import com.example.asset_tracker_backend.pricing.dto.CoinDto;
import com.example.asset_tracker_backend.pricing.dto.GoldPriceDto;
import com.example.asset_tracker_backend.pricing.dto.PriceUpdateResult;
import com.example.asset_tracker_backend.pricing.dto.StockSearchResult;
import com.example.asset_tracker_backend.pricing.provider.CoinGeckoService;
import com.example.asset_tracker_backend.pricing.provider.MaybankGoldService;
import com.example.asset_tracker_backend.pricing.provider.StockPriceService;
import com.example.asset_tracker_backend.pricing.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Live price feeds from CoinGecko, Yahoo Finance, and Maybank Gold")
public class PricingController {

    private final CoinGeckoService coinGeckoService;
    private final StockPriceService stockPriceService;
    private final MaybankGoldService maybankGoldService;
    private final PriceService priceService;

    @GetMapping("/crypto/coins")
    @Operation(summary = "List all supported cryptocurrencies", description = "Returns the full CoinGecko coin list (cached 24h)")
    public ResponseEntity<List<CoinDto>> getCoinList() {
        return ResponseEntity.ok(coinGeckoService.getCoinList());
    }

    @GetMapping("/crypto/price")
    @Operation(summary = "Get crypto price", description = "Get price for specific coin IDs (comma-separated)")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String ids, @RequestParam(defaultValue = "usd") String currency) {
        List<String> coinIds = List.of(ids.split(","));
        return ResponseEntity.ok(coinGeckoService.getPrices(coinIds, currency));
    }

    @GetMapping("/stocks/search")
    @Operation(summary = "Search stocks", description = "Search for stocks/ETFs by name or symbol via Yahoo Finance")
    public ResponseEntity<List<StockSearchResult>> searchStocks(@RequestParam String q) {
        return ResponseEntity.ok(stockPriceService.searchStocks(q));
    }

    @GetMapping("/stocks/price")
    @Operation(summary = "Get stock price", description = "Get current price for a stock symbol")
    public ResponseEntity<?> getStockPrice(@RequestParam String symbol) {
        Double price = stockPriceService.getPrice(symbol);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(java.util.Map.of("symbol", symbol, "price", price, "currency", "USD"));
    }

    @GetMapping("/gold/price")
    @Operation(summary = "Get Maybank gold price", description = "Returns current Maybank gold selling/buying price in MYR per gram")
    public ResponseEntity<GoldPriceDto> getGoldPrice() {
        GoldPriceDto price = maybankGoldService.getGoldPrice();
        if (price == null) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(price);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Trigger price refresh", description = "Manually trigger a price update for all stored assets")
    public ResponseEntity<PriceUpdateResult> refreshPrices() {
        return ResponseEntity.ok(priceService.updateAllAssetPrices());
    }
}

