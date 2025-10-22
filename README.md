# Rentals API (Spring Boot 3.5.6, Java 21, jjwt 0.11.5)

API Back-End pour gérer des locations (rentals), les utilisateurs et la messagerie, avec authentification JWT et stockage des images.

## Technologies
- Java 21
- Maven 
- Spring Boot 
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL 
- Lombok
- Swagger/OpenAPI pour la documentation
- MultipartFile pour gestion des images
- Projet dévéloppé sous IntelliJ IDEA 2025.2

## Arborescence du projet
```
rentals-api/
├─ src/main/java/com/example/rentalsapi/
│  ├─ config/
│  │  ├─ OpenApi.java
│  │  ├─ TestUploadConfig.java
│  │  ├─ UploadInitializer.java
│  │  └─ WebConfig.java
│  ├─ controller/
│  │  ├─ AuthController.java
│  │  ├─ MessageController.java
│  │  ├─ RentalController.java
│  │  └─ UserController.java
│  ├─ dto/
│  │  ├─ AuthRequest.java 
│  │  ├─ AuthResponse.java 
│  │  ├─ AuthUserResponse.java
│  │  ├─ RegisterRequest.java
│  │  ├─ RegisterResponse.java
│  │  ├─ MessageRequest.java
│  │  ├─ MessageCreateResponse.java
│  │  ├─ MessageResponse.java
│  │  ├─ MessageListResponse.java
│  │  ├─ RentalRequest.java
│  │  ├─ RentalCreateResponse.java
│  │  ├─ RentalResponse.java
│  │  ├─ RentalListResponse.java
│  │  ├─ RentalUpdateRequest.java
│  │  ├─ RentalUpdateResponse.java
│  │  ├─ UserResponse.java
│  │  └─ UserListResponse.java
│  ├─ entity/
│  │  ├─ User.java
│  │  ├─ Rental.java
│  │  └─ Message.java
│  ├─ exception/
│  │  ├─ FileUploadExceptionHandler.java
│  │  ├─ GlobalExceptionHandler.java
│  │  ├─ UnauthorizedRentalAccessException.java
│  ├─ repository/
│  │  ├─ UserRepository.java
│  │  ├─ RentalRepository.java
│  │  └─ MessageRepository.java
│  ├─ service/
│  │  ├─ AuthService.java
│  │  ├─ UserService.java
│  │  ├─ RentalService.java
│  │  ├─ MessageService.java
│  │  ├─ SecurityService.java               # Récupère l'utilisateur via SecurityContext
│  │  └─ CustomUserDetailsService.java
│  └─ security/
│     ├─ JwtUtils.java
│     ├─ JwtFilter.java
│     └─ SecurityConfig.java
├─ src/main/resources/
│  ├─ application.properties
│  ├─ schema.sql                            # script de création de la base de données
│  ├─ data.sql (optionnel)                  # script de remplissage de la base de données
│  └─ static/images
│     └─default.jpg                         # image par défault
├─ src/test/java/com/example/rentalsapi/controller
│  ├─ AuthControllerTest.java               # teste les endpoints d'authentification
│  ├─ MessageControllerTest.java            # teste les endpoints liés à la gestion des messages
│  ├─ UserControllerTest.java               # teste les endpoints liés à la gestion des utilisateurs
│  └─ RentalControllerTest.java             # teste les endpoints liés à la gestion des locations

├─ src/test/resources/
│  └─ application-test.properties
└─ uploads/   # dossier pour stocker les images uploadées
```

 ## Configuration
Dans src/main/resources/application.properties :

### MySQL (La base de données *rentalsdb* doit exister)
spring.datasource.url=jdbc:mysql://localhost:3306/rentalsdb?useSSL=false&allowPublicKeyRetrieval=true        
spring.datasource.username={DB_USER}  
spring.datasource.password={DB_PASSWORD}               


## Endpoints

| Méthode | URL                | Description                                                     | Auth |
| ------- | ------------------ | --------------------------------------------------------------- | ---- |
| POST    | /api/auth/register | Crée un nouvel utilisateur                                      | Non  |
| POST    | /api/auth/login    | Connexion (retourne JWT)                                        | Non  |
| GET     | /api/auth/me       | Récupère les infos de l’utilisateur connecté                    | Oui  |
| GET     | /api/rentals       | Liste toutes les locations                                      | Oui  |
| GET     | /api/rentals/{id}  | Récupère une location par ID                                    | Oui  |
| POST    | /api/rentals       | Crée une location avec image                                    | Oui  |
| PUT     | /api/rentals/{id}  | Met à jour une location                                         | Oui  |
| POST    | /api/messages      | Crée un message                                                 | Oui  |
| GET     | /api/messages      | Récupère tous les messages de l’utilisateur                     | Oui  |
| GET     | /api/messages/{id} | Récupère un message si l’utilisateur est auteur ou propriétaire | Oui  |
| GET     | /api/users         | Liste tous les utilisateurs                                     | Oui  |
| GET     | /api/users/{id}    | Récupère un utilisateur par ID                                  | Oui  |



## Exemples de requêtes

1. Inscription

POST /api/auth/register  
Content-Type: application/json  

{  
"email": "user@example.com",  
"name": "John Doe",  
"password": "password123"  
}  

Réponse :  
{  
"token": "<JWT_TOKEN>"  
}

2. Connexion

POST /api/auth/login  
Content-Type: application/json  

{  
"email": "user@example.com",  
"password": "password123"  
}

Réponse :  
{  
"token": "<JWT_TOKEN>"  
}

3. Création d’un rental avec image

POST /api/rentals  
Authorization: Bearer <JWT_TOKEN>  
Content-Type: multipart/form-data  

name:Appartement T2  
surface:45.5  
price:500.0,  
description:Appartement lumineux  
picture: {fichier image}  

Réponse :

{  
"message": "Rental created !"  
}


## Swagger UI
- Accessible sur la route : /api/swagger-ui/index.html
- Permet de tester tous les endpoints et voir les modèles (DTO) utilisés.

## Sécurité et JWT
- JWT utilisé pour authentification stateless.
- Header requis :
  Authorization: Bearer <JWT_TOKEN>
- Routes auth/login et auth/register sont publiques.
- Toutes les autres routes nécessitent JWT valide.
- Erreurs courantes :
  * 401 Unauthorized → token absent ou invalide
  * 403 Forbidden → accès à un message ou rental non autorisé   

## Gestion des images
- Dossier des uploads : uploads
- Fichiers uploadés renommés avec UUID pour éviter les collisions.
- Si aucune image fournie, un fichier default.jpg est utilisé.

## Tests
### AuthControllerTest
Il garantit que :
- Le système d’authentification JWT fonctionne correctement.
- Les utilisateurs peuvent s’inscrire, se connecter, et accéder à leurs infos.
- Les endpoints protégés refusent bien les accès non authentifiés.
### MessageControllerTest
Il effectue les operations suivantes:
- Vérifier la sécurité JWT (accès restreint aux utilisateurs authentifiés).
- Tester les opérations principales sur les messages : création, lecture, consultation par ID.
- Contrôler les réponses d’erreur en cas d’accès interdit ou de données inexistantes.
### UserControllerTest
Il effectue les operations suivantes:
- Vérifier la sécurité d’accès aux utilisateurs (JWT obligatoire).
- Tester les réponses attendues des routes utilisateurs (/users, /users/{id}).
- Contrôler la bonne gestion des erreurs (404 pour utilisateur manquant).
### RentalControllerTest
Il effectue les operations suivantes:
- Vérifier la création, la lecture (liste + ID) et la mise à jour des rentals.
- Tester l’upload de fichiers images.
- Verifier que l’authentification JWT est bien requise pour ces actions.

## Commandes Maven 
- mvn clean install
- mvn spring-boot:run

## Notes
- Le Front-End pour cette API peut être recupêré dans : https://github.com/JulioDan57/P03-ChaTop-Front-end

## Author

Julio Daniel GIL CANO