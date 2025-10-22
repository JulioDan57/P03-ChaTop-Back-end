package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RentalUpdateRequest {

    @Schema(description = "Nom du rental", example = "Appartement T2")
    private String name;

    @Schema(description = "Surface en m²", example = "45.5")
    private Double surface;

    @Schema(description = "Prix en euros", example = "500.0")
    private Double price;

    @Schema(description = "Description du rental", example = "Appartement lumineux avec balcon")
    private String description;
}
