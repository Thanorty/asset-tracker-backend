package com.example.asset_tracker_backend.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoldPriceDto {
    private Double sellPrice;
    private Double buyPrice;
    private String currency;
    private String unit;
}

