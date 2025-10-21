package com.example.rentalsapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "MESSAGES")
@Getter @Setter @NoArgsConstructor
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID du message", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    @Schema(description = "Rental associé au message")
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Schema(description = "Auteur du message")
    private User user;

    @Column(length = 2000)
    @Schema(description = "Contenu du message", example = "Bonjour, est-ce que l'appartement est disponible ?")
    private String message;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
