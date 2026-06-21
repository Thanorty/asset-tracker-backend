package com.example.asset_tracker_backend.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate date;

    private String category;

    private String description;

    private Double amount;

    @Column(name = "recurring_expense")
    private Boolean recurringExpense;

    @Column(name = "end_date")
    private String endDate;
}
