package com.example.rentalsapi.controller;

import com.example.rentalsapi.dto.RentalRequest;
import com.example.rentalsapi.entity.Rental;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.MessageRepository;
import com.example.rentalsapi.repository.RentalRepository;
import com.example.rentalsapi.repository.UserRepository;
import com.example.rentalsapi.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🧪 Tests d’intégration du RentalController (base MySQL + JWT)
 * Ces tests ne s’exécutent qu’en environnement de test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentalControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RentalRepository rentalRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private JwtUtils jwtUtils;

    private String token;
    private User testUser;

    private final Path uploadDir = Paths.get("uploads-test");

    @BeforeAll
    void setupUserAndToken() {
        testUser = new User();
        testUser.setEmail("rentaltester@example.com");
        testUser.setName("Rental Tester");
        testUser.setPassword("password123");
        userRepository.save(testUser);

        token = jwtUtils.generateToken(testUser.getEmail());
    }

    @BeforeEach
    void ensureUploadDir() throws Exception {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @AfterEach
    void cleanUploads() throws Exception {
        System.out.println("🧹 Nettoyage de la base de test et du dossier d’uploads...");

        // Supprimer les entités dépendantes avant les rentals pour éviter les violations de contraintes
        messageRepository.deleteAll();
        rentalRepository.deleteAll();

        // Supprimer les fichiers uploadés
        if (Files.exists(uploadDir)) {
            Files.walk(uploadDir)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (file.delete()) {
                            System.out.println("🗑️ Fichier supprimé : " + file.getName());
                        }
                    });
        }
    }

    @Test
    @DisplayName("✅ POST /rentals — crée un rental avec image (retourne message seulement)")
    void shouldCreateRentalWithImage() throws Exception {
        MockMultipartFile picture = new MockMultipartFile(
                "picture", "photo.jpg", "image/jpeg", "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/rentals")
                        .file(picture)
                        .param("name", "Bel Appartement")
                        .param("surface", "80.0")
                        .param("price", "1200.0")
                        .param("description", "Appartement moderne et lumineux")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rental created !"));
    }

    @Test
    @DisplayName("✅ GET /rentals — retourne la liste des rentals")
    void shouldReturnAllRentals() throws Exception {
        Rental rental = new Rental();
        rental.setName("Test Rental");
        rental.setSurface(45.0);
        rental.setPrice(1000.0);
        rental.setDescription("Test description");
        rental.setOwner(testUser);
        rental.setCreatedAt(Timestamp.from(Instant.now()));
        rental.setUpdatedAt(Timestamp.from(Instant.now()));
        rentalRepository.save(rental);

        mockMvc.perform(get("/rentals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentals").isArray())
                .andExpect(jsonPath("$.rentals[*].name").value(org.hamcrest.Matchers.hasItem("Test Rental")));
    }

    @Test
    @DisplayName("✅ GET /rentals/{id} — retourne un rental existant")
    void shouldReturnRentalById() throws Exception {
        Rental rental = new Rental();
        rental.setName("Studio Test");
        rental.setSurface(30.0);
        rental.setPrice(700.0);
        rental.setDescription("Studio bien situé");
        rental.setOwner(testUser);
        rental.setCreatedAt(Timestamp.from(Instant.now()));
        rental.setUpdatedAt(Timestamp.from(Instant.now()));
        rentalRepository.save(rental);

        mockMvc.perform(get("/rentals/" + rental.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Studio Test"))
                .andExpect(jsonPath("$.price").value(700.0));
    }

    @Test
    @DisplayName("✅ PUT /rentals/{id} — met à jour un rental existant")
    void shouldUpdateRental() throws Exception {
        Rental rental = new Rental();
        rental.setName("Ancien Nom");
        rental.setSurface(60.0);
        rental.setPrice(900.0);
        rental.setDescription("Ancienne description");
        rental.setOwner(testUser);
        rental.setCreatedAt(Timestamp.from(Instant.now()));
        rental.setUpdatedAt(Timestamp.from(Instant.now()));
        rentalRepository.save(rental);

        mockMvc.perform(put("/rentals/" + rental.getId())
                        .param("name", "Nom mis à jour")
                        .param("surface", "65.0")
                        .param("price", "950.0")
                        .param("description", "Nouvelle description")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rental updated !"));
    }
}
