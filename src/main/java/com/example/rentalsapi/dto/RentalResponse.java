package com.example.rentalsapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RentalResponse {

    @Schema(description = "ID du rental", example = "1")
    private Long id;

    @Schema(description = "Nom du rental", example = "Appartement T2")
    private String name;

    @Schema(description = "Surface en m²", example = "45.5")
    private Double surface;

    @Schema(description = "Prix en euros", example = "500.0")
    private Double price;

    @Schema(description = "URL de l'image", example = "https://example.com/image.jpg")
    private String picture;

    @Schema(description = "Description du rental", example = "Appartement lumineux avec balcon")
    private String description;

    @Schema(description = "ID du propriétaire", example = "2")
    private Long ownerId;

    @Schema(description = "Date de création ", example = "2012/12/02")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @Schema(description = "Date de modification ", example = "2012/12/02")
    private Timestamp updatedAt;
}
