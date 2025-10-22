package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.UserListResponse;
import com.example.rentalsapi.dto.UserResponse;
import com.example.rentalsapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {

    @Autowired private UserService userService;

    @Operation(summary = "Récupérer tous les utilisateurs")
    @GetMapping
    public ResponseEntity<UserListResponse> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @Operation(summary = "Récupérer un utilisateur par ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
