package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.MessageCreateResponse;
import com.example.rentalsapi.dto.MessageListResponse;
import com.example.rentalsapi.dto.MessageRequest;
import com.example.rentalsapi.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MessageCreateResponse> create(@RequestBody MessageRequest req) {
        return ResponseEntity.ok(messageService.create(req));
    }

    @Operation(summary = "Récupérer tous les messages de l'utilisateur connecté")
    @GetMapping
    public ResponseEntity<MessageListResponse> getAllForUser(){
        return ResponseEntity.ok(messageService.getAllByUser());
    }

    @Operation(summary = "Voir un message si l'utilisateur est auteur ou propriétaire du rental")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var optionalMsg = messageService.getByIdIfOwnedByUser(id);
        if (optionalMsg.isPresent()) {
            return ResponseEntity.ok(optionalMsg.get());
        } else {
            return ResponseEntity.status(403).body("Forbidden or not found");
        }
    }

}
