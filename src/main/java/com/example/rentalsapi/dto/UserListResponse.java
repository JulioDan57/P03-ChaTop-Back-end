package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {

    @Schema(description = "Liste des utilisateurs")
    private List<UserResponse> users;
}
