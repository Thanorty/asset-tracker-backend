package com.example.asset_tracker_backend.admin.controller;

import com.example.asset_tracker_backend.admin.dto.CreateUserRequest;
import com.example.asset_tracker_backend.admin.dto.UserDto;
import com.example.asset_tracker_backend.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Admin-only endpoints for user management")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users")
    public List<UserDto> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminUserService.createUser(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update a user's role")
    public ResponseEntity<UserDto> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminUserService.updateRole(id, body.get("role")));
    }
}

