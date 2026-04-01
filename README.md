# UserAPI

> UserAPI provides REST endpoints for user authentication, profile management, and token handling, enabling developers to easily integrate secure user sessions and manage user data with Spring Boot services.

## Features

- User authentication
- Real-time messaging
- User profile management
- Token refresh and logout
- Centralized exception handling
- Role-based authorization
- Secure token blacklisting
- Data validation and constraints
- JWT-based sessions
- RESTful API endpoints

## API

> Full API documentation is available via Swagger UI after starting the application.
> See the **Getting Started** section below to run the server.

## Tech Stack

- Docker
- JWT
- Lombok
- Maven
- PostgreSQL
- SQL
- Spring Boot
- Spring Framework
- Spring Security

## Project Structure

```
UserAPI/
├── .github
│   └── workflows
│       └── ci.yml
├── gradle
│   └── wrapper
│       └── gradle-wrapper.properties
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org
│   │   │       └── example
│   │   │           └── authapi
│   │   │               ├── config
│   │   │               │   ├── AppConfig.java
│   │   │               │   ├── DataInitializer.java
│   │   │               │   └── OpenApiConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   └── UserController.java
│   │   │               ├── dto
│   │   │               │   ├── JWTResponse.java
│   │   │               │   ├── LoginRequest.java
│   │   │               │   ├── LogoutRequest.java
│   │   │               │   ├── MessageResponse.java
│   │   │               │   ├── RefreshTokenRequest.java
│   │   │               │   ├── RegisterRequest.java
│   │   │               │   ├── UpdateUserRequest.java
│   │   │               │   └── UserProfileResponse.java
│   │   │               ├── exception
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── TokenRefreshException.java
│   │   │               ├── model
│   │   │               │   ├── JwtBlacklist.java
│   │   │               │   ├── RefreshToken.java
│   │   │               │   ├── Role.java
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   ├── JWTBlacklistRepository.java
│   │   │               │   ├── RefreshTokenRepository.java
│   │   │               │   ├── RoleRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── security
│   │   │               │   ├── JWTAuthenticationFilter.java
│   │   │               │   ├── JWTUtils.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── service
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── JWTBlacklistService.java
│   │   │               │   ├── UserService.java
│   │   │               │   └── UserServiceImpl.java
│   │   │               └── AuthApiApplication.java
│   │   └── resources
│   │       ├── db
│   │       │   └── migration
│   │       │       ├── V1__create_roles_table.sql
│   │       │       ├── V2__create_users_table.sql
│   │       │       ├── V3__create_user_roles_table.sql
│   │       │       ├── V4__create_refresh_tokens_table.sql
│   │       │       └── V5__create_jwt_blacklist_table.sql
│   │       └── application.properties
│   └── test
│       └── java
│           └── org
│               └── example
│                   └── authapi
│                       ├── controller
│                       │   ├── AuthControllerTest.java
│                       │   └── UserControllerTest.java
│                       ├── service
│                       │   ├── AuthServiceTest.java
│                       │   ├── JWTBlacklistServiceTest.java
│                       │   └── UserServiceImplTest.java
│                       └── AuthApiApplicationTests.java
├── .gitattributes
├── .gitignore
├── Dockerfile
├── README.md
├── build.gradle
├── docker-compose.yaml
├── gradlew
├── gradlew.bat
└── settings.gradle
```

| File | Description |
|------|-------------|
| `authapi/AuthApiApplication.java` | This file, AuthApiApplication, serves as the entry point for a Spring Boot application. It |
| `controller/UserController.java` | This file defines two endpoints in the `UserController` class: `getMe()` handles GET reque |
| `controller/AuthController.java` | AuthController.java defines REST endpoints for user registration, login, token refresh, an |
| `exception/GlobalExceptionHandler.java` | This file contains a `GlobalExceptionHandler` class annotated with `@ControllerAdvice` for |
| `service/JWTBlacklistService.java` | This file defines a service class `JWTBlacklistService` for managing blacklisted JWTs, usi |
| `service/UserServiceImpl.java` | This file, `UserServiceImpl.java`, implements the `UserService` interface and provides met |
| `service/UserService.java` | The `UserService` interface defines two methods: `getUserProfile(String username)` to retr |
| `service/AuthService.java` | The AuthService class handles user authentication and authorization tasks in Spring Boot, |
| `security/JWTAuthenticationFilter.java` | The `JWTAuthenticationFilter` class extends `OncePerRequestFilter` to handle JWT-based aut |
| `repository/RoleRepository.java` | This file defines a `RoleRepository` interface extending Spring Data JPA's `JpaRepository` |
| `repository/UserRepository.java` | This file defines the `UserRepository` interface, extending Spring's JpaRepository for bas |
| `repository/RefreshTokenRepository.java` | This file defines a Spring Data JPA repository for `RefreshToken` management, including me |
| `repository/JWTBlacklistRepository.java` | This file defines a Spring Data JPA repository, `JWTBlacklistRepository`, for managing bla |
| `security/JWTUtils.java` | This Java class, JWTUtils.java, handles JWT generation and validation. Key methods include |
| `dto/RefreshTokenRequest.java` | The `RefreshTokenRequest` class represents a request for token refresh and includes getter |
| `dto/RegisterRequest.java` | This file defines `RegisterRequest.java`, a data transfer object for user registration in |
| `dto/LogoutRequest.java` | `LogoutRequest` is a Java DTO class in `org.example.authapi.dto` with fields for `jwt` and |
| `dto/LoginRequest.java` | `LoginRequest.java` defines a data transfer object for user login requests, containing `@N |
| `dto/MessageResponse.java` | `MessageResponse.java` is a data transfer object for sending text messages as responses. T |
| `dto/JWTResponse.java` | The JWTResponse class in the org.example.authapi.dto package represents a JSON Web Token r |
| `dto/UpdateUserRequest.java` | The `UpdateUserRequest` class in the `org.example.authapi.dto` package defines properties |
| `dto/UserProfileResponse.java` | `UserProfileResponse.java` is a Data Transfer Object (DTO) in the 'authapi' project, conta |
| `model/JwtBlacklist.java` | The JwtBlacklist class models a database entity with three fields: |
| `model/User.java` | The User class is a JPA entity with fields for id, username, email, and password, annotate |
| `model/RefreshToken.java` | The RefreshToken class in Java is a JPA entity with fields for ID, user, token, and expiry |
| `model/Role.java` | The file defines the `Role` class as a JPA entity with an ID and name field, using an `ERo |
| `exception/TokenRefreshException.java` | The `TokenRefreshException.java` file defines an empty class `TokenRefreshException` in th |
| `resources/application.properties` | This file configures a Spring Boot application named AuthAPI, setting up database connecti |
| `config/AppConfig.java` | `AppConfig.java` configures password encoding using BCrypt in a Spring Boot application. I |
| `config/OpenApiConfig.java` | This file configures an OpenAPI specification in a Spring Boot project using Swagger UI. I |

## Getting Started

### Setup Your Development Environment

1. **Clone the Repository**
   ```sh
   git clone https://github.com/your-repo/auth-api.git
   cd auth-api
   ```

2. **Build and Run with Docker**
   Ensure you have Docker installed, then run:
   ```sh
   docker-compose up --build
   ```

3. **Configure Database Connection**
   Update the `application.properties` file to match your PostgreSQL database details:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   ```

4. **Migrate the Database**
   Run Flyway migrations to set up the database schema:
   ```sh
   docker-compose exec auth-api mvn flyway:migrate
   ```

5. **Start the Application**
   Use Maven to run the Spring Boot application:
   ```sh
   mvn spring-boot:run
   ```

6. **Test Your API Endpoints**
   Use tools like Postman or cURL to test your REST endpoints, starting with user registration and login.

### Additional Notes

- Ensure you have Docker, Maven, PostgreSQL, and Spring Boot installed.
- The `JWTBlacklistService` helps manage blacklisted tokens for enhanced security.
