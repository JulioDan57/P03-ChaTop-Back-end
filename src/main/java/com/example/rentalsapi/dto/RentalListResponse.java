package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RentalListResponse {

    @Schema(description = "Liste des biens immobiliers")
    private List<RentalResponse> rentals;
}
