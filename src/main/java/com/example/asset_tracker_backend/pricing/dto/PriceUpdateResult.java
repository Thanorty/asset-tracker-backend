package com.example.asset_tracker_backend.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceUpdateResult {
    private int updated;
    private int failed;
    private String message;
}

