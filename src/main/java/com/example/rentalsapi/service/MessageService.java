package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.MessageCreateResponse;
import com.example.rentalsapi.dto.MessageListResponse;
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
    @Autowired private SecurityService securityService;

    public MessageCreateResponse create(MessageRequest req) {
        User user = securityService.getCurrentUser();
        Rental rental = rentalRepo.findById(req.getRentalId()).orElseThrow(() -> new RuntimeException("Rental not found"));

        Message msg = new Message();
        msg.setMessage(req.getMessage());
        msg.setUser(user);
        msg.setRental(rental);
        msg.setCreatedAt(Timestamp.from(Instant.now()));
        msg.setUpdatedAt(Timestamp.from(Instant.now()));
        messageRepo.save(msg);

        return new MessageCreateResponse("Message send with success");
    }

    /**
     * Récupère tous les messages **envoyés** par l'utilisateur identifié par userEmail.
     */
    public MessageListResponse getAllByUser() {
        User user = securityService.getCurrentUser();
        List<MessageResponse> messages = messageRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        MessageListResponse response = new MessageListResponse();
        response.setMessages(messages);
        return response;
    }


    /**
     * Récupère un message si l'utilisateur (userEmail) est
     * - soit l'auteur du message (message.user)
     * - soit le propriétaire du rental concerné (message.rental.owner)
     *
     * Renvoie Optional.empty() si le message n'existe pas ou si l'utilisateur n'a pas les droits.
     */
    public Optional<MessageResponse> getByIdIfOwnedByUser(Long id) {
        User currentUser = securityService.getCurrentUser();
        Optional<Message> opt = messageRepo.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        Message msg = opt.get();

        boolean isAuthor = msg.getUser() != null && currentUser.getEmail().equals(msg.getUser().getEmail());
        boolean isRentalOwner = msg.getRental() != null
                && msg.getRental().getOwner() != null
                && currentUser.getEmail().equals(msg.getRental().getOwner().getEmail());

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
