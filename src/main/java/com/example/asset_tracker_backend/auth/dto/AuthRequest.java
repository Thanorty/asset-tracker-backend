package com.example.asset_tracker_backend.auth.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}

