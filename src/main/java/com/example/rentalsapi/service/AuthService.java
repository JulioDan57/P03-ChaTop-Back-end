package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.RegisterRequest;
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

    public String register(RegisterRequest req) {
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

        return jwtUtils.generateToken(u.getEmail());
    }

    public String login(String email, String password) {
        // ✅ Utilise Spring Security pour authentifier
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Si aucune exception n’est levée, credentials valides
        return jwtUtils.generateToken(email);
    }

    public User getByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow();
    }
}
