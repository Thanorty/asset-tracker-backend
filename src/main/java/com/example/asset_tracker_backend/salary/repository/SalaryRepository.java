package com.example.asset_tracker_backend.salary.repository;

import com.example.asset_tracker_backend.salary.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByUserId(Long userId);
}

