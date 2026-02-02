package com.example.asset_tracker_backend.expense.service;

import com.example.asset_tracker_backend.expense.model.Expense;

import java.util.List;

public interface ExpenseService {

    Expense createExpense(Expense expense);

    List<Expense> getAllExpenses();

    void deleteExpense(Long id);

    Double getMonthlyTotal(int year, int month);

    List<Object[]> getCategorySummary();
}