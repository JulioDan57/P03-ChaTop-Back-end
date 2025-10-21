package com.example.rentalsapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageRequest {

    @Schema(description = "Contenu du message", example = "Bonjour, est-ce que l'appartement est disponible ?")
    private String message;

    @Schema(description = "ID de l'utilisateur auteur", example = "1")
    private Long userId;

    @Schema(description = "ID du rental associé au message", example = "1")
    private Long rentalId;
}