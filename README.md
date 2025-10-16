# Bank REST API

Простой REST API для управления пользователями и банковскими картами с ролями `ROLE_ADMIN` и `ROLE_USER`, реализованный на **Spring Boot**, **PostgreSQL**, с использованием **JWT** для аутентификации и **Liquibase** для миграций базы данных.

---

## Оглавление

- [Описание проекта](#описание-проекта)
- [Технологии](#технологии)
- [Запуск проекта](#запуск-проекта)
- [Структура проекта](#структура-проекта)
- [API](#api)
- [Роли и права](#роли-и-права)

---

## Описание проекта

Проект представляет собой банковскую систему с базовой функциональностью:

- Регистрация и логин пользователей (`ADMIN` и `USER`)
- Управление пользователями (`ADMIN`)
- Создание, блокировка, разблокировка карт (`ADMIN`)
- Просмотр и управление своими картами (`USER`)
- Переводы между картами (`USER`)
- Логи и управление транзакциями через базу данных

---

## Технологии

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Liquibase**
- **Docker / Docker Compose** (для базы данных)
- **REST API**
- **Maven** (сборка проекта)

---

## Запуск проекта

### 1. Клонирование репозитория
```bash
git clone <твоя ссылка на репозиторий>
cd bank_rest
```

### 2. Запуск PostgreSQL через Docker
```bash
docker compose up -d
```

### 3. Настройка application.properties

Проверьте подключение к базе данных:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/bank_db
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

### 4. Запуск приложения
```bash
mvn clean install
mvn spring-boot:run
```
Приложение будет доступно на http://localhost:8080

⸻

### Структура проекта
```
src/main/java/com/example/bankcards
├─ controller      # REST контроллеры
├─ dto             # DTO объекты
├─ entity          # Сущности JPA
├─ repository      # Репозитории JPA
├─ security        # JWT, SecurityConfig, фильтры
├─ service         # Сервисы бизнес-логики
└─ BankRestApplication.java # Главный класс приложения

src/main/resources
├─ db/migration    # Liquibase миграции
└─ application.properties
```

## API

### Регистрация ADMIN
```
POST /api/auth/register
```

### Регистрация USER
```
POST /api/auth/register
```

### Логин
```
POST /api/auth/login
```

⸻

### Пользователи (только ADMIN)
•	GET `/api/users/get/all` — Получить всех пользователей    
•	DELETE `/api/users/delete/all` — Удалить всех пользователей   

⸻

### Карты
•	POST `/api/cards/create` — Создать карту (ADMIN)  
•	POST `/api/cards` — Создать карту для пользователя (ADMIN)    
•	GET `/api/cards/all` — Получить все карты (ADMIN)     
•	PUT `/api/cards/{cardId}/block` — Заблокировать карту (ADMIN)     
•	PUT `/api/cards/{cardId}/unblock` — Разблокировать карту (ADMIN)      
•	GET `/api/cards` — Получить свои карты (USER)          
•	PUT `/api/cards/{cardId}/requestBlock` — Запрос на блокировку карты (USER)    
•	GET `/api/cards/{cardId}/balance` — Получить баланс карты (USER)  
•	POST `/api/cards/transfer` — Перевод денег с карты на карту (USER)    
•	DELETE `/api/cards/{cardId}` — Удалить карту (ADMIN)  

### Пример запроса перевода денег:
```json
{
  "fromCardId": 11,
  "toCardId": 12,
  "amount": 100.0
}
```

### Роли и права

Роль            Доступные действия  
ROLE_ADMIN      Управление пользователями, создание и блокировка карт   
ROLE_USER       Просмотр своих карт, баланс, запрос блокировки, переводы    

### Примечания
•	JWT токен требуется во всех защищённых эндпоинтах (Authorization: Bearer <token>)   
•	Все изменения базы данных выполняются через Liquibase   
•	Администратор создаётся при первом запуске приложения через миграции или вручную через /auth/register   