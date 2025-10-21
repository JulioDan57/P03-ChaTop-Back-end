package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.RentalRequest;
import com.example.rentalsapi.dto.RentalResponse;
import com.example.rentalsapi.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rentals")
@Tag(name = "Rentals", description = "Gestion des locations")
public class RentalController {

    @Autowired private RentalService rentalService;

    // ----------------------------
    // 🔹 CREATE — avec image
    // ----------------------------
    @Operation(summary = "Créer une location avec image")
    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestPart(name = "picture", required = false) MultipartFile pictureFile
    ) throws IOException {

        // Récupération de l'email de l'utilisateur connecté depuis le SecurityContext
        String ownerEmail = getAuthenticatedUserEmail();

        RentalRequest req = new RentalRequest();
        req.setName(name);
        req.setSurface(surface);
        req.setPrice(price);
        req.setDescription(description);

        rentalService.create(ownerEmail, req, pictureFile);

        return ResponseEntity.ok(Map.of("message", "Rental created !"));
    }

    // ----------------------------
    // 🔹 UPDATE — sans image
    // ----------------------------
    @Operation(summary = "Modifier une location (sans image)")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description
    ) throws IOException {

        String ownerEmail = getAuthenticatedUserEmail();

        RentalRequest req = new RentalRequest();
        req.setName(name);
        req.setSurface(surface);
        req.setPrice(price);
        req.setDescription(description);

        return rentalService.update(id, req, ownerEmail, null)
                .map(r -> ResponseEntity.ok(Map.of("message", "Rental updated !")))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ----------------------------
    // 🔹 GET ALL
    // ----------------------------
    @Operation(summary = "Récupérer toutes les locations")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<RentalResponse> rentals = rentalService.getAll();
        return ResponseEntity.ok(java.util.Map.of("rentals", rentals));
    }

    // ----------------------------
    // 🔹 GET BY ID
    // ----------------------------
    @Operation(summary = "Récupérer une location par ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getById(@PathVariable Long id) {
        return rentalService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------
    // Méthode utilitaire
    // -------------------
    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); // correspond à l'email car c'est le principal
    }
}
