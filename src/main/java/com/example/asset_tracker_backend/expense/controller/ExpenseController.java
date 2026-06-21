package com.example.asset_tracker_backend.expense.controller;

import com.example.asset_tracker_backend.auth.repository.UserRepository;
import com.example.asset_tracker_backend.expense.dto.CategoryExpenseDto;
import com.example.asset_tracker_backend.expense.model.Expense;
import com.example.asset_tracker_backend.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Track and manage daily expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Record a new expense")
    public Expense createExpense(@RequestBody Expense expense) {
        // Get the currently authenticated username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Fetch the user by username and set their ID on the expense
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        expense.setUserId(user.getId().intValue());
        
        return expenseService.createExpense(expense);
    }

    @GetMapping
    @Operation(summary = "List all expenses", description = "Returns all expenses (cached)")
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense by ID")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly total", description = "Returns total spending for a given year and month")
    public Double getMonthlyExpense(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return expenseService.getMonthlyTotal(year, month);
    }

    @GetMapping("/category-summary")
    @Operation(summary = "Expense by category", description = "Returns spending grouped by category (cached)")
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