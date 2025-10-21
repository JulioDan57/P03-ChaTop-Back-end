package com.example.rentalsapi.service;

import com.example.rentalsapi.dto.RentalRequest;
import com.example.rentalsapi.dto.RentalResponse;
import com.example.rentalsapi.entity.Rental;
import com.example.rentalsapi.entity.User;
import com.example.rentalsapi.exception.UnauthorizedRentalAccessException;
import com.example.rentalsapi.repository.RentalRepository;
import com.example.rentalsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Value("${app.upload.dir}")
    //@Value("${server.servlet.context-path}" + "/" + "${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.default-image:default.jpg}")
    private String defaultImageName; // Nom du fichier image par défaut

    @Autowired private RentalRepository rentalRepo;
    @Autowired private UserRepository userRepo;

    /**
     * Crée un rental avec éventuellement une image
     */
    public RentalResponse create(String ownerEmail, RentalRequest req, MultipartFile pictureFile) throws IOException {
        User owner = userRepo.findByEmail(ownerEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Rental rental = new Rental();
        rental.setName(req.getName());
        rental.setSurface(req.getSurface());
        rental.setPrice(req.getPrice());
        rental.setDescription(req.getDescription());
        rental.setOwner(owner);
        rental.setCreatedAt(Timestamp.from(Instant.now()));
        rental.setUpdatedAt(Timestamp.from(Instant.now()));

        // Gestion du fichier image
        if (pictureFile != null && !pictureFile.isEmpty()) {
            rental.setPicture(saveFile(pictureFile));
        } else if (req.getPicture() != null && !req.getPicture().isEmpty()) {
            rental.setPicture(Paths.get(req.getPicture()).getFileName().toString());
        } else {
            rental.setPicture(defaultImageName);
        }

        return toDto(rentalRepo.save(rental));
    }

    /**
     * Met à jour un rental et éventuellement l'image
     */
    public Optional<RentalResponse> update(Long id, RentalRequest req, String ownerEmail, MultipartFile pictureFile) throws IOException {
        Optional<Rental> opt = rentalRepo.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        Rental rental = opt.get();
        if (!rental.getOwner().getEmail().equals(ownerEmail)) {
            throw new UnauthorizedRentalAccessException();
        }

        rental.setName(req.getName());
        rental.setSurface(req.getSurface());
        rental.setPrice(req.getPrice());
        rental.setDescription(req.getDescription());
        rental.setUpdatedAt(Timestamp.from(Instant.now()));

        // Supprime ancienne image si elle n'est pas la default et nouvelle fournie
        if (pictureFile != null && !pictureFile.isEmpty()) {
            deleteOldFile(rental.getPicture());
            rental.setPicture(saveFile(pictureFile));
        } else if (req.getPicture() != null && !req.getPicture().isEmpty()) {
            rental.setPicture(Paths.get(req.getPicture()).getFileName().toString());
        } else if (rental.getPicture() == null) {
            rental.setPicture(defaultImageName);
        }

        return Optional.of(toDto(rentalRepo.save(rental)));
    }

    /** Supprime un fichier si ce n’est pas l’image par défaut */
    private void deleteOldFile(String filename) throws IOException {
        if (filename != null && !filename.equals(defaultImageName)) {
            Path oldFilePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(oldFilePath);
        }
    }

    /** Sauvegarde le fichier et retourne le nom unique */
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".")))
                .orElse("");

        String uniqueFilename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename; // on stocke juste le nom du fichier
    }

    /**
     * Conversion entity -> DTO en générant l'URL complète de l'image
     */
    private RentalResponse toDto(Rental r) {
        RentalResponse dto = new RentalResponse();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setSurface(r.getSurface());
        dto.setPrice(r.getPrice());
        dto.setDescription(r.getDescription());

        // Choisir le fichier image à afficher
        String filename = (r.getPicture() != null && !r.getPicture().isEmpty())
                ? r.getPicture()
                : defaultImageName;

        // Nettoyer uploadDir pour éviter double slash
        String cleanFolder = uploadDir.startsWith("/") ? uploadDir.substring(1) : uploadDir;
        if (!cleanFolder.endsWith("/")) cleanFolder += "/";

        // Construire l’URL publique
        dto.setPicture(ServletUriComponentsBuilder.fromCurrentContextPath()
                //.replacePath("")   // <- supprime le context-path (/api)
                .path("/" + cleanFolder + filename)
                .toUriString());

        if (r.getOwner() != null) {
            dto.setOwnerId(r.getOwner().getId());
        }

        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());

        return dto;
    }



    public Optional<RentalResponse> getById(Long id) {
        return rentalRepo.findById(id).map(this::toDto);
    }

    public List<RentalResponse> getAll() {
        return rentalRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
