package com.example.asset_tracker_backend.salary.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SalaryResponse {
    private Long id;
    private Double grossSalary;
    private String salaryCycle;
    private Integer cycleDay;
    private List<DeductionDetail> deductions;
    private Double totalDeductions;
    private Double netSalary;

    @Data
    @Builder
    public static class DeductionDetail {
        private Long id;
        private String name;
        private String deductionType;
        private Double value;
        private Double computedAmount;
    }
}
