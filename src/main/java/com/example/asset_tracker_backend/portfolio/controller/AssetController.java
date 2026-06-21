package com.example.asset_tracker_backend.portfolio.controller;

import com.example.asset_tracker_backend.auth.repository.UserRepository;
import com.example.asset_tracker_backend.portfolio.model.Asset;
import com.example.asset_tracker_backend.portfolio.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "Manage investment assets (crypto, stocks, gold)")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Add a new asset", description = "Creates a new investment asset entry")
    public Asset createAsset(@RequestBody Asset asset) {
        // Get the currently authenticated username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user by username and set their ID on the expense
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        asset.setUserId(user.getId().intValue());

        return assetService.createAsset(asset);
    }

    @GetMapping
    @Operation(summary = "List all assets", description = "Returns all investment assets (cached)")
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete asset", description = "Deletes an asset by ID")
    public void deleteAsset(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        assetService.deleteAsset(id, user.getId().intValue());
    }
}
