package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.MessageRequest;
import com.example.rentalsapi.entity.Message;
import com.example.rentalsapi.entity.Rental;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.MessageRepository;
import com.example.rentalsapi.repository.RentalRepository;
import com.example.rentalsapi.repository.UserRepository;
import com.example.rentalsapi.security.JwtUtils;
import com.example.rentalsapi.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepo;
    @Autowired private RentalRepository rentalRepo;
    @Autowired private MessageRepository messageRepo;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private MessageService messageService;

    private String userToken;
    private String ownerToken;
    private User user;
    private User owner;
    private Rental rental;

    @BeforeAll
    void setupUsersAndRental() {
        // Créer un utilisateur test
        user = new User();
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setCreatedAt(Timestamp.from(Instant.now()));
        userRepo.save(user);
        userToken = jwtUtils.generateToken(user.getEmail());

        // Créer un propriétaire du rental
        owner = new User();
        owner.setEmail("owner@test.com");
        owner.setName("Owner User");
        owner.setPassword("password");
        owner.setCreatedAt(Timestamp.from(Instant.now()));
        userRepo.save(owner);
        ownerToken = jwtUtils.generateToken(owner.getEmail());

        // Créer un rental associé au propriétaire
        rental = new Rental();
        rental.setName("Test Rental");
        rental.setPrice(1000.0);
        rental.setSurface(50.0);
        rental.setDescription("Test description");
        rental.setOwner(owner);
        rental.setCreatedAt(Timestamp.from(Instant.now()));
        rentalRepo.save(rental);
    }

    @AfterEach
    void cleanMessages() {
        messageRepo.deleteAll();
    }

    @AfterAll
    void cleanUsersAndRentals() {
        rentalRepo.deleteAll();
        userRepo.deleteAll();
    }

    // ===================== TESTS MESSAGES =====================

    @Test
    @DisplayName("✅ POST /api/messages — créer un message (validate message success)")
    void shouldCreateMessage() throws Exception {
        MessageRequest req = new MessageRequest();
        req.setRentalId(rental.getId());
        req.setMessage("Hello owner!");

        mockMvc.perform(post("/messages")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message send with success"));

        // Vérifier que le message a bien été persistant
        List<Message> messages = messageRepo.findByUserId(user.getId());
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getMessage()).isEqualTo("Hello owner!");
    }

    @Test
    @DisplayName("✅ GET /api/messages — retourne les messages de l'utilisateur authentifié")
    void shouldGetMessagesForUser() throws Exception {
        // --- Arrange : créer un message pour cet utilisateur ---
        Message msg = new Message();
        msg.setMessage("Test message");
        msg.setRental(rental);
        msg.setUser(user);
        msg.setCreatedAt(Timestamp.from(Instant.now()));
        msg.setUpdatedAt(Timestamp.from(Instant.now()));
        messageRepo.save(msg);

        // --- Act + Assert ---
        mockMvc.perform(get("/messages")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // vérifie la présence du tableau messages
                .andExpect(jsonPath("$.messages").isArray())
                // vérifie qu'au moins un message est présent
                .andExpect(jsonPath("$.messages[0].message").value("Test message"))
                .andExpect(jsonPath("$.messages[0].rental_id").value(rental.getId()))
                .andExpect(jsonPath("$.messages[0].id").exists())
                .andExpect(jsonPath("$.messages[0].created_at").exists());
    }


    @Test
    @DisplayName("✅ GET /api/messages/{id} — retourne un message spécifique")
    void shouldGetMessageById() throws Exception {
        // --- Arrange : créer un message manuellement ---
        Message msg = new Message();
        msg.setMessage("Hello there!");
        msg.setRental(rental);
        msg.setUser(user);
        msg.setCreatedAt(Timestamp.from(Instant.now()));
        msg.setUpdatedAt(Timestamp.from(Instant.now()));
        messageRepo.save(msg);

        // --- Act + Assert ---
        mockMvc.perform(get("/messages/" + msg.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Vérifie le contenu du message renvoyé
                .andExpect(jsonPath("$.id").value(msg.getId()))
                .andExpect(jsonPath("$.message").value("Hello there!"))
                .andExpect(jsonPath("$.rental_id").value(rental.getId()))
                .andExpect(jsonPath("$.created_at").exists());
    }



    // ===================== TESTS ERREURS =====================

    @Test
    @DisplayName("❌ POST /api/messages — sans token renvoie 401")
    void createMessageWithoutToken() throws Exception {
        MessageRequest req = new MessageRequest();
        req.setRentalId(1L);
        req.setMessage("Test message");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Full authentication is required to access this resource"));
    }

    @Test
    @DisplayName("❌ POST /api/messages — avec token invalide renvoie 401")
    void createMessageWithInvalidToken() throws Exception {
        MessageRequest req = new MessageRequest();
        req.setRentalId(1L);
        req.setMessage("Test message");

        mockMvc.perform(post("/messages")
                        .header("Authorization", "Bearer invalidtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Full authentication is required to access this resource"));
    }

    @Test
    @DisplayName("❌ GET /api/messages/{id} — message inexistant renvoie 403")
    void getNonExistentMessage() throws Exception {
        long nonExistentId = 999L;

        mockMvc.perform(get("/messages/" + nonExistentId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden or not found"));
    }

    @Test
    @DisplayName("❌ GET /api/messages/{id} — message non autorisé renvoie 403")
    void getMessageForbidden() throws Exception {
        // Créer un autre utilisateur
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setName("Other User");
        otherUser.setPassword("password");
        userRepo.save(otherUser);

        // Créer et sauvegarder un rental pour cet utilisateur
        Rental otherRental = new Rental();
        otherRental.setName("Other Rental");
        otherRental.setSurface(50.0);
        otherRental.setPrice(500.0);
        otherRental.setDescription("Description");
        otherRental.setOwner(otherUser);
        rentalRepo.save(otherRental);

        // Créer directement un message via repository
        Message msgEntity = new Message();
        msgEntity.setMessage("Private message");
        msgEntity.setUser(otherUser);
        msgEntity.setRental(otherRental);
        msgEntity.setCreatedAt(Timestamp.from(Instant.now()));
        msgEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        messageRepo.save(msgEntity);

        // Essayer de le récupérer avec le token de testUser
        mockMvc.perform(get("/messages/" + msgEntity.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden or not found"));
    }
}
