package com.example.rentalsapi.service;

import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired private UserRepository userRepository;

    /**
     * Récupère l'utilisateur complet à partir du token JWT (via SecurityContext)
     */
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
