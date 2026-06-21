package com.example.asset_tracker_backend.salary.controller;

import com.example.asset_tracker_backend.salary.dto.SalaryRequest;
import com.example.asset_tracker_backend.salary.dto.SalaryResponse;
import com.example.asset_tracker_backend.salary.service.SalaryService;
import com.example.asset_tracker_backend.auth.model.User;
import com.example.asset_tracker_backend.auth.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "Manage salary and dynamic deductions")
public class SalaryController {

    private final SalaryService salaryService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get current user's salary config")
    public ResponseEntity<SalaryResponse> getSalary(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return salaryService.getByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping
    @Operation(summary = "Create or update salary with dynamic deductions")
    public ResponseEntity<SalaryResponse> saveSalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SalaryRequest request) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(salaryService.saveOrUpdate(userId, request));
    }

    private Long resolveUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

