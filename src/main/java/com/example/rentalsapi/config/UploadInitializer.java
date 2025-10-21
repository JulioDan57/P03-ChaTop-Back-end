package com.example.rentalsapi.config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadInitializer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Vérifie si default.jpg existe déjà dans uploads
        Path defaultFile = uploadPath.resolve("default.jpg");
        if (!Files.exists(defaultFile)) {
            // Copie depuis src/main/resources/static/images/default.jpg
            ClassPathResource resource = new ClassPathResource("static/images/default.jpg");
            Files.copy(resource.getInputStream(), defaultFile);
        }
    }
}
