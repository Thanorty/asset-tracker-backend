package com.example.asset_tracker_backend.portfolio.service;

import com.example.asset_tracker_backend.portfolio.model.Asset;
import com.example.asset_tracker_backend.portfolio.repository.AssetRepository;
import com.example.asset_tracker_backend.pricing.provider.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    public static final String USD = "USD";
    public static final String MYR = "MYR";
    private final AssetRepository assetRepository;
    private final CurrencyConverter currencyConverter;

    @Override
    public Asset createAsset(Asset asset) {
        asset.setCreatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    @Override
    public List<Asset> getAllAssets() {
        List<Asset> assets = assetRepository.findAll();

        if (assets.isEmpty()) {
            return assets;
        }

        Double conversionRate = currencyConverter.convert(USD);
        for (Asset asset : assets) {

            // Only convert if the asset has a price update and is currently in USD
            if (asset.getLastPriceUpdate() != null && asset.getCurrency().equals(USD)) {
                asset.setCurrentValue(asset.getQuantity() * asset.getUnitPrice() * conversionRate);
                asset.setUnitPrice(asset.getUnitPrice() * conversionRate);
                asset.setBuyPrice(asset.getBuyPrice() * conversionRate);
                asset.setCurrency(MYR);
            }
        }

        return assets;
    }

    @Override
    public void deleteAsset(Long id, Integer userId) {
        var asset = assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found or not owned by the current user"));
        assetRepository.delete(asset);
    }

    @Override
    public List<Object[]> getAllocationByType() {
        return assetRepository.getAllocationByType();
    }
}
