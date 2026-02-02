package com.example.asset_tracker_backend.dashboard.service;

import com.example.asset_tracker_backend.dashboard.dto.DashboardSummaryDto;
import com.example.asset_tracker_backend.expense.service.ExpenseService;
import com.example.asset_tracker_backend.portfolio.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseService expenseService;
    private final AssetService assetService;

    @Override
    public DashboardSummaryDto getDashboardSummary(int year, int month) {

        Double monthlyExpenses = expenseService.getMonthlyTotal(year, month);

        Map<String, Double> expenseByCategory = new HashMap<>();
        expenseService.getCategorySummary()
                .forEach(row ->
                        expenseByCategory.put(
                                (String) row[0],
                                (Double) row[1]
                        )
                );

        Map<String, Double> assetAllocation = new HashMap<>();
        assetService.getAllocationByType()
                .forEach(row ->
                        assetAllocation.put(
                                (String) row[0],
                                (Double) row[1]
                        )
                );

        Double totalPortfolioValue =
                assetAllocation.values().stream().mapToDouble(Double::doubleValue).sum();

        return new DashboardSummaryDto(
                totalPortfolioValue,
                monthlyExpenses,
                assetAllocation,
                expenseByCategory
        );
    }
}
