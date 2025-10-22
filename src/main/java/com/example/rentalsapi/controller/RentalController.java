package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.*;
import com.example.rentalsapi.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<RentalCreateResponse> create(
            @ModelAttribute RentalRequest req,
            @RequestPart(name = "picture", required = false) MultipartFile pictureFile) throws IOException {
        return ResponseEntity.ok(rentalService.create(req, pictureFile));
    }

    // ----------------------------
    // 🔹 UPDATE — sans image
    // ----------------------------
    @Operation(summary = "Modifier une location (sans image)")
    @PutMapping("/{id}")
    public ResponseEntity<RentalUpdateResponse> update(
            @PathVariable Long id,
            @ModelAttribute RentalUpdateRequest req
    ) throws IOException {
        return rentalService.update(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ----------------------------
    // 🔹 GET ALL
    // ----------------------------
    @Operation(summary = "Récupérer toutes les locations")
    @GetMapping
    public ResponseEntity<RentalListResponse> getAll() {
        return ResponseEntity.ok(rentalService.getAll());
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
}
