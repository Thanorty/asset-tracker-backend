package com.example.asset_tracker_backend.dashboard.service;

import com.example.asset_tracker_backend.dashboard.dto.DashboardSummaryDto;

public interface DashboardService {

    DashboardSummaryDto getDashboardSummary(int year, int month);
}
