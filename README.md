# Banking REST API

A simple REST API for managing users and bank cards with the `ROLE_ADMIN` and `ROLE_USER` roles, implemented in **Spring Boot**, **PostgreSQL**, using **JWT** for authentication and **Liquibase** for database migration.

---

## Title

- [Project Description](#project-description)  
- [Technologies](#technologies)  
- [Project Launch](#project-launch)  
- [Project Structure](#project-structure)  
- [API](#api)  
- [Roles and Permissions](#roles-and-permissions)  

---

## Project Description

The project is a banking system with basic functionality:

- User registration and login (`ADMIN` and `USER`)  
- User management (`ADMIN`)  
- Card creation, blocking, and unblocking (`ADMIN`)  
- Viewing and managing your cards (`USER`)  
- Transfers between cards (`USER`)  
- Transaction logs and management via database  

---

## Technologies

- **Java 17**  
- **Spring Boot 3**  
- **Spring Security + JWT**  
- **Spring Data JPA / Hibernate**  
- **PostgreSQL**  
- **Liquibase**  
- **Docker / Docker Compose** (for the database)  
- **REST API**  
- **Maven** (project build)  

---

## Running the Project

### 1. Cloning the Repository
```bash
git clone <your repository link>
cd bank_rest
```

### 2. Running PostgreSQL via Docker
```bash
docker compose up -d
```

### 3. Configuring Application.yml

Testing the Database Connection:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/bank_db
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

### 4. Running the Application
```bash
mvn clean install
mvn spring-boot:run
```
The application will be available at http://localhost:8080

⸻

### Project Structure
```
src/main/java/com/example/bankcards
├─ controller      # REST controllers
├─ dto             # DTO objects
├─ entity          # JPA entities
├─ repository      # JPA repositories
├─ security        # JWT, SecurityConfig, filters
├─ service         # Services Business logic
└─ BankRestApplication.java # Main application class

src/main/resources
├─ db/migration    # Liquibase migration
└─ application.yml
```

## API

### ADMIN Registration
```
POST /api/auth/register
```

### USER Registration
```
POST /api/auth/register
```

### Login
```
POST /api/auth/login
```

⸻

### Users (admin only)
•	GET `/api/users/get/all` — Get all users  
•	DELETE `/api/users/delete/all` — Delete all users 

⸻

### Cards
• POST `/api/cards/create` — Create a card (ADMIN)    
• POST `/api/cards` — Create a card for a user (ADMIN)  
• GET `/api/cards/all` — Get all cards (ADMIN)  
• PUT `/api/cards/{cardId}/block` — Block a card (ADMIN).  
• PUT `/api/cards/{cardId}/unblock` — Unblock a card (ADMIN).  
• GET `/api/cards` — Get your cards (USER)  
• PUT `/api/cards/{cardId}/requestBlock` — Request to block a card (USER)  
• GET `/api/cards/{cardId}/balance` — Get a card balance (USER)  
• POST `/api/cards/transfer` — Transfer money from card to card (USER)  
• DELETE `/api/cards/{cardId}` — Delete a card (ADMIN).  

### Money transfer example request:
```json
{
  "fromCardId": 11,
  "toCardId": 12,
  "amount": 100.0
}
```

### Roles and Permissions

Role         Available Actions  
ROLE_ADMIN   User management, card creation and blocking  
ROLE_USER    View card symbol, balance, block request, transfers    

### Notes
• A JWT token is required for all secure endpoints (authorization: bearer <token>).  
• All database changes are performed through Liquibase.  
• An administrator is created by pre-authorizing the application via a conference or manually via /auth/register.  
