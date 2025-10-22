package com.example.rentalsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MessageListResponse {

    @Schema(description = "Liste des messages")
    private List<MessageResponse> messages;
}
