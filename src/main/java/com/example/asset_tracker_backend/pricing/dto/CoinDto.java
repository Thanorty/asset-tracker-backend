package com.example.asset_tracker_backend.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinDto {
    private String id;
    private String symbol;
    private String name;
}

