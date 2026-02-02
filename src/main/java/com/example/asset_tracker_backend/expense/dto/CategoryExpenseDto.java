package com.example.asset_tracker_backend.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryExpenseDto {
    private String category;
    private Double total;
}
