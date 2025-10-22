package com.example.rentalsapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthUserResponse {

    @Schema(description = "ID de l'utilisateur", example = "1")
    private Long id;

    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;

    @Schema(description = "Nom complet de l'utilisateur", example = "John Doe")
    private String name;

    @Schema(description = "Date de création", example = "2025-10-06T12:00:00")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Timestamp createdAt;

    @Schema(description = "Date de dernière mise à jour", example = "2025-10-06T12:30:00")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Timestamp updatedAt;

}
