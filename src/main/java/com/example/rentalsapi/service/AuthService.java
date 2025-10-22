package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.*;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.UserRepository;
import com.example.rentalsapi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest req) {
        userRepo.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email already in use");
        });

        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setCreatedAt(Timestamp.from(Instant.now()));
        u.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepo.save(u);

        return new RegisterResponse(jwtUtils.generateToken(u.getEmail()));
    }

    public AuthResponse login(AuthRequest req) {
        // ✅ Utilise Spring Security pour authentifier
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // Si aucune exception n’est levée, credentials valides
        return new AuthResponse(jwtUtils.generateToken(req.getEmail()));
    }

    public AuthUserResponse getCurrentUser(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        AuthUserResponse dto = new AuthUserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public User getByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow();
    }

}
