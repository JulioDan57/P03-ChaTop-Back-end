package com.example.rentalsapi.controller;

import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.MessageRepository;
import com.example.rentalsapi.repository.RentalRepository;
import com.example.rentalsapi.repository.UserRepository;
import com.example.rentalsapi.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private MessageRepository messageRepo;
    @Autowired private RentalRepository rentalRepo;
    @Autowired private JwtUtils jwtUtils;

    private User user;
    private String userToken;

    @BeforeAll
    void setupUser() {
        user = new User();
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setCreatedAt(Timestamp.from(Instant.now()));
        userRepo.save(user);
        userToken = jwtUtils.generateToken(user.getEmail());
    }

    @AfterAll
    void cleanUsers() {
        messageRepo.deleteAll();
        rentalRepo.deleteAll();
        userRepo.deleteAll();
    }


    @Test
    @DisplayName("✅ GET /api/users — liste des utilisateurs (protégée)")
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users[0].email").exists());
    }

    @Test
    @DisplayName("✅ GET /api/users/{id} — utilisateur par ID (protégée)")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/" + user.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    @DisplayName("❌ GET /api/users/{id} — utilisateur inexistant renvoie 404")
    void getNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
