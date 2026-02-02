package com.example.asset_tracker_backend.dashboard.controller;

import com.example.asset_tracker_backend.dashboard.dto.DashboardSummaryDto;
import com.example.asset_tracker_backend.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDto getSummary(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return dashboardService.getDashboardSummary(year, month);
    }
}