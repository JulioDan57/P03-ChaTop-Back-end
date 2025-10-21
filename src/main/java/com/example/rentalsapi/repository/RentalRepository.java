package com.example.rentalsapi.repository;

import com.example.rentalsapi.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
