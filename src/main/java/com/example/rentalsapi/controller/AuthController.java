package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.AuthRequest;
import com.example.rentalsapi.dto.RegisterRequest;
import com.example.rentalsapi.dto.UserResponse;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth") //remplacer par /auth
@Tag(name = "Auth", description = "Endpoints pour l'authentification et la gestion des utilisateurs")
public class AuthController {

    @Autowired private AuthService authService;
    //@Autowired private JwtUtils jwtUtils;

    @Operation(summary = "Créer un nouvel utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        String token = authService.register(req);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Se connecter avec email et mot de passe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Récupérer les informations de l'utilisateur connecté")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informations récupérées"),
            @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.getByEmail(email);
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(response);
    }
}

