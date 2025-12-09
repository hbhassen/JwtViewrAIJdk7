package com.example.jwtapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValidateRequest(
        @NotBlank(message = "token is required")
        @Size(max = 4096, message = "token is too long")
        String token) {
}
