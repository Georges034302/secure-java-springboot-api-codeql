CONTRIBUTING.md

Contributing Guide for UserApp (Spring Boot + Maven)

- Prerequisites: Java 21, Maven 3.8+
- Fork the repository and clone your fork:
  git clone <your-fork-url>
  cd session2/java/UserApp

- Build the project:
  mvn clean install

- Run the application:
  ./mvnw spring-boot:run
  or
  mvn spring-boot:run

- Code style:
  - Follow Java and Spring Boot conventions
  - Add Javadoc to public classes and methods
  - Organize imports

- Testing:
  - Add or update unit tests for new features and bug fixes

- Submitting changes:
  - Push your branch to your fork
  - Open a pull request against the main repository
  - Ensure all CI checks pass

- Reporting issues:
  - Use GitHub Issues for bugs and feature requests
  - Provide clear steps to reproduce and expected behavior
- Test 1:
  - code ql
  - dependentbot
  