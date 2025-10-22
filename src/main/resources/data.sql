-- Données de test pour USERS. Le mot de passe pour tous est 1234
INSERT INTO USERS (id, name, email, password, created_at, updated_at) VALUES
(1, 'Alice Dupont', 'alice@example.com', '$2a$10$1Xbcijit27uwgH3afeXCKeqCuDWXwlxSQLFD9jpwEh6uAexUYl/8q', NOW(), NOW()),
(2, 'Bob Martin', 'bob@example.com', '$2a$10$NEdry5PVx3BLrQYM9jPfquQHWo3jTxobi.AoaALl53PuvmsRB7ptC', NOW(), NOW()),
(3, 'Charlie Durand', 'charlie@example.com', '$2a$10$SD0uuTuGBHFotyjyRFwPCevPE4GV8cXQdfCnOYSDAoxxdJ5lFk4Me', NOW(), NOW());

-- Données de test pour RENTALS
INSERT INTO RENTALS (id, name, surface, price, picture, description, owner_id, created_at, updated_at) VALUES
(1, 'Appartement cosy', 45, 650, 'cf4566b0-333d-47c0-bd6d-89f99288bded.jpg', 'Bel appartement situé au centre-ville', 1, NOW(), NOW()),
(2, 'Maison familiale', 120, 1200, '15b7e6d1-f0cb-4be5-a410-dc6d468c0d2a.jpg', 'Grande maison idéale pour une famille', 2, NOW(), NOW()),
(3, 'Studio étudiant', 25, 400, 'c6becca1-443b-4f42-a745-f4c4efa3d141.jpg', 'Petit studio pratique proche université', 3, NOW(), NOW());

-- Données de test pour MESSAGES
INSERT INTO MESSAGES (id, rental_id, user_id, message, created_at, updated_at) VALUES
(1, 1, 2, 'Bonjour, est-ce que l’appartement est toujours disponible ?', NOW(), NOW()),
(2, 2, 1, 'La maison est-elle meublée ?', NOW(), NOW()),
(3, 3, 2, 'Est-ce que le studio accepte les animaux ?', NOW(), NOW());
