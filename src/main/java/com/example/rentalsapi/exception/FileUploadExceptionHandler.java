package com.example.rentalsapi.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Component
public class FileUploadExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        String message = String.format(
                "Le fichier est trop volumineux ! Taille maximale par fichier : %s, taille maximale totale de la requête : %s.",
                formatSize(maxFileSize), formatSize(maxRequestSize)
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur serveur : " + ex.getMessage());
    }

    /**
     * Convertit une taille de type "5MB", "2GB", "500KB" en Mo lisible
     */
    private String formatSize(String size) {
        if (size == null || size.isEmpty()) return "inconnue";

        size = size.toUpperCase().trim();
        try {
            if (size.endsWith("KB")) {
                double kb = Double.parseDouble(size.replace("KB", ""));
                return String.format("%.2f Mo", kb / 1024);
            } else if (size.endsWith("MB")) {
                double mb = Double.parseDouble(size.replace("MB", ""));
                return String.format("%.2f Mo", mb);
            } else if (size.endsWith("GB")) {
                double gb = Double.parseDouble(size.replace("GB", ""));
                return String.format("%.2f Mo", gb * 1024);
            } else {
                // par défaut on suppose que c’est en octets
                double bytes = Double.parseDouble(size);
                return String.format("%.2f Mo", bytes / (1024 * 1024));
            }
        } catch (NumberFormatException e) {
            return size; // fallback si conversion impossible
        }
    }
}
