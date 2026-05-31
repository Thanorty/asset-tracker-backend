package com.example.asset_tracker_backend.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSearchResult {
    private String symbol;
    private String name;
    private String exchange;
    private String type;
}

