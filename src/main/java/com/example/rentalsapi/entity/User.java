package com.example.rentalsapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter @Setter @NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de l'utilisateur", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    private String name;

    @JsonIgnore
    @Schema(description = "Mot de passe de l'utilisateur", example = "password123")
    private String password;

    @Schema(description = "Date de création")
    private Timestamp createdAt;

    @Schema(description = "Date de mise à jour")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Rental> rentals;
}
