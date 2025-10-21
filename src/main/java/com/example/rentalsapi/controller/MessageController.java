package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.MessageRequest;
import com.example.rentalsapi.dto.MessageResponse;
import com.example.rentalsapi.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@Tag(name = "Messages", description = "Gestion des messages des utilisateurs")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "Créer un message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message créé"),
            @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    @PostMapping  // e@RequestHeader(name = "Authorization"
    public ResponseEntity<?> create(@RequestBody MessageRequest req) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        messageService.create(userEmail, req);
        return ResponseEntity.ok(Map.of("message", "Message send with success"));
    }

    @Operation(summary = "Récupérer tous les messages de l'utilisateur connecté")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllForUser(){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MessageResponse> messages = messageService.getAllByUser(userEmail);
        return ResponseEntity.ok(java.util.Map.of("messages", messages));
    }

    @Operation(summary = "Voir un message si l'utilisateur est auteur ou propriétaire du rental")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        var optionalMsg = messageService.getByIdIfOwnedByUser(id, userEmail);

        if (optionalMsg.isPresent()) {
            return ResponseEntity.ok(optionalMsg.get());
        } else {
            return ResponseEntity.status(403).body("Forbidden or not found");
        }
    }

}
