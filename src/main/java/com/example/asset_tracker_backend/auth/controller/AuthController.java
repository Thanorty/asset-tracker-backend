package com.example.asset_tracker_backend.auth.controller;

import com.example.asset_tracker_backend.auth.dto.AuthRequest;
import com.example.asset_tracker_backend.auth.dto.AuthResponse;
import com.example.asset_tracker_backend.auth.dto.RegisterRequest;
import com.example.asset_tracker_backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

