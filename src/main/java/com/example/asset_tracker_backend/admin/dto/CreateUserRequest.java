package com.example.asset_tracker_backend.admin.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String role; // USER or ADMIN
}

