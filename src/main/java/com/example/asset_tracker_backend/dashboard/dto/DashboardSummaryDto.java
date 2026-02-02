package com.example.asset_tracker_backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardSummaryDto {

    private Double totalPortfolioValue;

    private Double monthlyExpenses;

    private Map<String, Double> assetAllocation;

    private Map<String, Double> expenseByCategory;
}