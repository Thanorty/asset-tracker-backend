package com.example.asset_tracker_backend.expense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

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

    @Column(name = "recurring")
    private Boolean recurringExpense;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @NotNull
    @Column(name = "user_id")
    private Integer userId;
}
