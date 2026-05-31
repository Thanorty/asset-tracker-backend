package com.example.asset_tracker_backend.dashboard.controller;

import com.example.asset_tracker_backend.dashboard.dto.DashboardSummaryDto;
import com.example.asset_tracker_backend.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated financial summary")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Returns portfolio value, monthly expenses, asset allocation, and expense breakdown")
    public DashboardSummaryDto getSummary(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return dashboardService.getDashboardSummary(year, month);
    }
}