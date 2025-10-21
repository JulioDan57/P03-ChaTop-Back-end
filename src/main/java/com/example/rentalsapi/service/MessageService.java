package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.MessageRequest;
import com.example.rentalsapi.dto.MessageResponse;
import com.example.rentalsapi.entity.Message;
import com.example.rentalsapi.entity.Rental;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.MessageRepository;
import com.example.rentalsapi.repository.RentalRepository;
import com.example.rentalsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired private MessageRepository messageRepo;
    @Autowired private RentalRepository rentalRepo;
    @Autowired private UserRepository userRepo;

    public void create(String userEmail, MessageRequest req) {
        User user = userRepo.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Rental rental = rentalRepo.findById(req.getRentalId()).orElseThrow(() -> new RuntimeException("Rental not found"));

        Message msg = new Message();
        msg.setMessage(req.getMessage());
        msg.setUser(user);
        msg.setRental(rental);
        msg.setCreatedAt(Timestamp.from(Instant.now()));
        msg.setUpdatedAt(Timestamp.from(Instant.now()));

        Message saved = messageRepo.save(msg);

    }

    /**
     * Récupère tous les messages **envoyés** par l'utilisateur identifié par userEmail.
     */
    public List<MessageResponse> getAllByUser(String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Récupère un message si l'utilisateur (userEmail) est
     * - soit l'auteur du message (message.user)
     * - soit le propriétaire du rental concerné (message.rental.owner)
     *
     * Renvoie Optional.empty() si le message n'existe pas ou si l'utilisateur n'a pas les droits.
     */
    public Optional<MessageResponse> getByIdIfOwnedByUser(Long id, String userEmail) {
        Optional<Message> opt = messageRepo.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        Message msg = opt.get();

        boolean isAuthor = msg.getUser() != null && userEmail.equals(msg.getUser().getEmail());
        boolean isRentalOwner = msg.getRental() != null
                && msg.getRental().getOwner() != null
                && userEmail.equals(msg.getRental().getOwner().getEmail());

        if (isAuthor || isRentalOwner) {
            return Optional.of(toDto(msg));
        }

        return Optional.empty();
    }

    private MessageResponse toDto(Message msg) {
        MessageResponse dto = new MessageResponse();
        dto.setId(msg.getId());
        dto.setRentalId(msg.getRental().getId());
        dto.setUserId(msg.getUser().getId());
        dto.setUserEmail(msg.getUser().getEmail());
        dto.setMessage(msg.getMessage());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}
