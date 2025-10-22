package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    @Schema(description = "Jeton JWT généré après l'inscription")
    private String token;
}
