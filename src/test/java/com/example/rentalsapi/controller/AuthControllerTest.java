package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.AuthRequest;
import com.example.rentalsapi.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🔐 Tests d'intégration AuthController avec MySQL
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("✅ /api/auth/register - crée un utilisateur et retourne un token JWT")
    void shouldRegisterUserAndReturnToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("testuser@example.com");
        request.setName("Test User");
        request.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        String token = node.get("token").asText();

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("✅ /api/auth/login - authentifie un utilisateur et retourne un token JWT")
    void shouldLoginUserAndReturnToken() throws Exception {
        // Étape 1 : inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("loginuser@example.com");
        registerRequest.setName("Login User");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Étape 2 : connexion
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("loginuser@example.com");
        authRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        String token = node.get("token").asText();

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("✅ /api/auth/me - retourne les infos complètes de l'utilisateur authentifié")
    void shouldReturnCurrentUserFullInfo() throws Exception {
        // Étape 1 : inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("meuser@example.com");
        registerRequest.setName("Me User");
        registerRequest.setPassword("password123");

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = registerResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(jsonResponse).get("token").asText();

        // Étape 2 : requête /api/auth/me avec le token
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("meuser@example.com"))
                .andExpect(jsonPath("$.name").value("Me User"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    @DisplayName("❌ /api/auth/me - retourne 401 si aucun token n'est fourni")
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Full authentication is required")));
    }
}
