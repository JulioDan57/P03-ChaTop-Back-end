package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    @Schema(description = "Jeton JWT d'authentification", example = "eyJhbGciOiJIUzI1...")
    private String token;
}
