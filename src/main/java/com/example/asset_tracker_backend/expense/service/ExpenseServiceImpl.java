package com.example.asset_tracker_backend.expense.service;

import com.example.asset_tracker_backend.expense.model.Expense;
import com.example.asset_tracker_backend.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public Double getMonthlyTotal(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return expenseRepository.getTotalExpenseBetween(start, end);
    }

    @Override
    public List<Object[]> getCategorySummary() {
        return expenseRepository.getExpenseGroupedByCategory();
    }
}
