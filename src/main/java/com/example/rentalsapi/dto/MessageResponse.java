package com.example.rentalsapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageResponse {

    @Schema(description = "ID du message", example = "1")
    private Long id;

    @Schema(description = "ID du rental associé", example = "1")
    private Long rentalId;

    @Schema(description = "ID de l'utilisateur auteur", example = "2")
    private Long userId;

    @Schema(description = "Email de l'utilisateur auteur", example = "user@example.com")
    private String userEmail;

    @Schema(description = "Contenu du message", example = "Bonjour, est-ce que l'appartement est disponible ?")
    private String message;

    @Schema(description = "Date de création du message")
    private Timestamp createdAt;

}
