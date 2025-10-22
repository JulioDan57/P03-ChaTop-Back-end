package com.example.rentalsapi.repository;

import com.example.rentalsapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Récupérer tous les messages d'un utilisateur
    List<Message> findByUserId(Long userId);

    // 🔹 Méthodes triées par date de création décroissante
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);
}
