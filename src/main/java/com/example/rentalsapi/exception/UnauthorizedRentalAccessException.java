package com.example.rentalsapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedRentalAccessException extends RuntimeException {
    public UnauthorizedRentalAccessException() {
        super("You are not authorized to modify this rental.");
    }
}
