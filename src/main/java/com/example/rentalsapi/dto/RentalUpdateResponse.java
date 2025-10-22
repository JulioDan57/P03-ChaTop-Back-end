package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalUpdateResponse {

    @Schema(description = "Message de confirmation", example = "Rental updated !")
    private String message;
}
