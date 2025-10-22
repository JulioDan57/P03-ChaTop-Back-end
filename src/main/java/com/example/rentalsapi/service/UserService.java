package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.UserListResponse;
import com.example.rentalsapi.dto.UserResponse;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;

    public UserListResponse getAll() {
        UserListResponse response = new UserListResponse();
        response.setUsers(userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        return response;
    }

    public Optional<UserResponse> getById(Long id) {
        return userRepo.findById(id).map(this::toDto);
    }

    private UserResponse toDto(User u) {
        UserResponse dto = new UserResponse();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setName(u.getName());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }
}
