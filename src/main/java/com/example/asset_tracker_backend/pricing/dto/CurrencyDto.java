package com.example.asset_tracker_backend.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {
    private String date;
    private String base;
    private String quote;
    private double rate;
}

