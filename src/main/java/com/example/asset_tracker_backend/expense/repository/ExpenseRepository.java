package com.example.asset_tracker_backend.expense.repository;

import com.example.asset_tracker_backend.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
        SELECT SUM(e.amount)
        FROM Expense e
        WHERE e.date BETWEEN :start AND :end
    """)
    Double getTotalExpenseBetween(LocalDate start, LocalDate end);

    @Query("""
        SELECT e.category, SUM(e.amount)
        FROM Expense e
        GROUP BY e.category
    """)
    List<Object[]> getExpenseGroupedByCategory();

    @Query("""
        SELECT e.category, SUM(e.amount)
        FROM Expense e
        WHERE e.date BETWEEN :start AND :end
        GROUP BY e.category
    """)
    List<Object[]> getExpenseGroupedByCategory(LocalDate start, LocalDate end);
}
