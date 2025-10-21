package com.example.rentalsapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.File;

@Configuration
public class TestUploadConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
