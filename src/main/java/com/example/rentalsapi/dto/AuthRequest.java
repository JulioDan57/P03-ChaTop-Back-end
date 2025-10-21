package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequest {

    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur", example = "password123")
    private String password;
}
