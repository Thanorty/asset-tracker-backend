package com.example.asset_tracker_backend.expense.controller;

import com.example.asset_tracker_backend.expense.dto.CategoryExpenseDto;
import com.example.asset_tracker_backend.expense.model.Expense;
import com.example.asset_tracker_backend.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

    @GetMapping("/monthly")
    public Double getMonthlyExpense(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return expenseService.getMonthlyTotal(year, month);
    }

    @GetMapping("/category-summary")
    public List<CategoryExpenseDto> getCategorySummary() {
        return expenseService.getCategorySummary()
                .stream()
                .map(row -> new CategoryExpenseDto(
                        (String) row[0],
                        (Double) row[1]
                ))
                .toList();
    }

}