INSTRUCTIONS.md

Setup instructions:
- Prerequisites: Java 21, Maven 3.8+
- Clone the repository:
  git clone <your-repo-url>
  cd session2/java/UserApp
- Configure application properties:
  Edit src/main/resources/application.properties for API key, database, and Hibernate settings

How to run the app:
- ./mvnw spring-boot:run
- or: mvn spring-boot:run

API endpoints:
- GET /api/user — Get user by email (query param: email)
- POST /api/user — Create user (JSON body)
- PUT /api/user — Update user (JSON body)
- DELETE /api/user — Delete user by email (query param: email)

Technologies used:
- Spring Boot 3.x
- Java 21
- Maven
- Spring Web
- Spring Data JPA
- H2 Database
- GitHub