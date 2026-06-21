package com.example.asset_tracker_backend.portfolio.service;

import com.example.asset_tracker_backend.portfolio.model.Asset;

import java.util.List;

public interface AssetService {

    Asset createAsset(Asset asset);

    List<Asset> getAllAssets();

    void deleteAsset(Long id, Integer userId);

    List<Object[]> getAllocationByType();
}