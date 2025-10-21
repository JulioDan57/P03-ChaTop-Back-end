package com.example.rentalsapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "RENTALS")
@Getter @Setter @NoArgsConstructor
public class Rental {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID du rental", example = "1")
    private Long id;

    @Schema(description = "Nom du rental", example = "Appartement T2")
    private String name;

    @Schema(description = "Surface en m²", example = "45.5")
    private Double surface;

    @Schema(description = "Prix du rental", example = "500.0")
    private Double price;

    @Schema(description = "URL de l'image", example = "https://example.com/image.jpg")
    private String picture;

    @Column(length = 2000)
    @Schema(description = "Description du rental", example = "Appartement lumineux avec balcon")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Propriétaire du rental")
    private User owner;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
