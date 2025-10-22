package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.*;
import com.example.rentalsapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth") //remplacer par /auth
@Tag(name = "Auth", description = "Endpoints pour l'authentification et la gestion des utilisateurs")
public class AuthController {

    @Autowired private AuthService authService;

    @Operation(summary = "Créer un nouvel utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(summary = "Se connecter avec email et mot de passe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(summary = "Récupérer les informations de l'utilisateur connecté")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informations récupérées"),
            @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }
}

