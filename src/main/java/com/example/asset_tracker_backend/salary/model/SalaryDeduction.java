package com.example.asset_tracker_backend.salary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_deductions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    @JsonIgnore
    private Salary salary;

    @Column(nullable = false)
    private String name;

    @Column(name = "deduction_type", nullable = false)
    private String deductionType; // PERCENTAGE or FIXED

    @Column(nullable = false)
    private Double value;
}

