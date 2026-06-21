package com.example.asset_tracker_backend.salary.service;

import com.example.asset_tracker_backend.salary.dto.SalaryRequest;
import com.example.asset_tracker_backend.salary.dto.SalaryResponse;
import com.example.asset_tracker_backend.salary.model.Salary;
import com.example.asset_tracker_backend.salary.model.SalaryDeduction;
import com.example.asset_tracker_backend.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;

    public Optional<SalaryResponse> getByUserId(Long userId) {
        return salaryRepository.findByUserId(userId).map(this::toResponse);
    }

    @Transactional
    public SalaryResponse saveOrUpdate(Long userId, SalaryRequest request) {
        Salary salary = salaryRepository.findByUserId(userId).orElseGet(() -> {
            Salary s = new Salary();
            s.setUserId(userId);
            return s;
        });

        salary.setGrossSalary(request.getGrossSalary());
        salary.setSalaryCycle(request.getSalaryCycle() != null ? request.getSalaryCycle() : "MONTHLY");
        salary.setCycleDay(request.getCycleDay() != null ? request.getCycleDay() : 25);

        // Replace all deductions
        salary.getDeductions().clear();
        if (request.getDeductions() != null) {
            for (SalaryRequest.DeductionDto dto : request.getDeductions()) {
                SalaryDeduction d = new SalaryDeduction();
                d.setSalary(salary);
                d.setName(dto.getName());
                d.setDeductionType(dto.getDeductionType() != null ? dto.getDeductionType() : "PERCENTAGE");
                d.setValue(dto.getValue() != null ? dto.getValue() : 0.0);
                salary.getDeductions().add(d);
            }
        }

        salary = salaryRepository.save(salary);
        return toResponse(salary);
    }

    private SalaryResponse toResponse(Salary salary) {
        double gross = salary.getGrossSalary();
        var details = new ArrayList<SalaryResponse.DeductionDetail>();
        double totalDeductions = 0;

        for (SalaryDeduction d : salary.getDeductions()) {
            double computed = "PERCENTAGE".equals(d.getDeductionType())
                    ? gross * d.getValue() / 100.0
                    : d.getValue();
            totalDeductions += computed;
            details.add(SalaryResponse.DeductionDetail.builder()
                    .id(d.getId())
                    .name(d.getName())
                    .deductionType(d.getDeductionType())
                    .value(d.getValue())
                    .computedAmount(Math.round(computed * 100.0) / 100.0)
                    .build());
        }

        return SalaryResponse.builder()
                .id(salary.getId())
                .grossSalary(gross)
                .salaryCycle(salary.getSalaryCycle())
                .cycleDay(salary.getCycleDay())
                .deductions(details)
                .totalDeductions(Math.round(totalDeductions * 100.0) / 100.0)
                .netSalary(Math.round((gross - totalDeductions) * 100.0) / 100.0)
                .build();
    }
}

