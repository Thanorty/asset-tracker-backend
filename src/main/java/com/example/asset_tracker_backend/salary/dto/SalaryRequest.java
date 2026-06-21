package com.example.asset_tracker_backend.salary.dto;

import lombok.Data;

import java.util.List;

@Data
public class SalaryRequest {
    private Double grossSalary;
    private String salaryCycle;  // MONTHLY, BI_WEEKLY, WEEKLY
    private Integer cycleDay;
    private List<DeductionDto> deductions;

    @Data
    public static class DeductionDto {
        private String name;
        private String deductionType; // PERCENTAGE or FIXED
        private Double value;
    }
}
