# 🚀 Secure Spring Boot API with GitHub Copilot, CodeQL & GitHub Security

This demo guides instructors through building a secure, layered Spring Boot application (`UserApp`) using GitHub Copilot and GitHub Security tools. It includes model, repository, service, and controller components with security scans and CI/CD integration.

---

## ✅ Step 1: Scaffold the Spring Boot Project

> **Prompt:**  
> “Use Spring Initializr to generate a Maven-based Spring Boot project named `UserApp` with:
> - Java 21
> - Dependencies: Spring Web, Spring Data JPA, H2 Database
> - Output as a ZIP
> - Unzip it into `/workspaces/ghcp-course/UserApp`, then delete the ZIP.”

```bash
cd /workspaces/ghcp-course/UserApp
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,h2 \
  -d name=UserApp \
  -d artifactId=UserApp \
  -d type=maven-project \
  -d language=java \
  -d javaVersion=21 \
  -o UserApp.zip
unzip UserApp.zip
rm UserApp.zip
```

> ✅ **Expected Output:**
> ```
> UserApp/
> ├── src/
> │   ├── main/
> │   │   ├── java/com/example/UserApp/
> │   │   └── resources/
> │   └── test/
> ├── pom.xml
> └── mvnw, mvnw.cmd
> ```

---

## ✅ Step 2: Define the Clean Architecture

> **Prompt:**  
> Generate a standard Spring Boot architecture for a REST API named `UserApp` that includes:  
> - **Model:** `User` entity with fields: `id (Long)`, `email (String)`, and `name (String)`  
> - `id` should be the primary key (`@Id`)  
> - **Repository:** `UserRepository` that extends `JpaRepository<User, Long>`  
> - **Service:** `UserService` with method `getUserById(Long id)`  
> - **Controller:** `UserController` mapped to `/api/user/{id}`, returning user info by ID  
> - Use `touch` to create the Java class files (in a single `bash` display block)  
> - Provide the full Java class code for each component in separate `java` blocks  



> ✅ **Expected Output:**
> ```
> com.example.UserApp
> ├── controller/UserController.java
> ├── service/UserService.java
> ├── model/User.java
> └── repository/UserRepository.java
> ```

---

## ✅ Step 3: Add Configuration

> **Prompt:**  
> “Create `application.properties` with:
> - `app.api.key`
> - H2 DB connection settings
> - Hibernate config for auto schema generation”

> ✅ **Expected Output (`src/main/resources/application.properties`):**
```properties
app.api.key=sk_configured_123
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create
spring.h2.console.enabled=true
```

---

## ✅ Step 4: Demonstrate Insecure Version (Before Copilot Fix)

> **Prompt:**  
> “Create an insecure version of `UserController`: 
> - Use `Statement` and string concatenation for SQL query
> - Hardcode secret in the class”

> ✅ **Expected Output (simplified vulnerable controller):**
```java
private static final String API_KEY = "sk_test_123";

@GetMapping("/user")
public ResponseEntity<String> getUser(@RequestParam String email) throws SQLException {
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email = '" + email + "'");
    return rs.next() ? ResponseEntity.ok("User Found") : ResponseEntity.notFound().build();
}
```

---

## ✅ Step 5: Refactor Using GitHub Copilot

> **Prompt:**  
> “Is this vulnerable to SQL injection?”  
> “Refactor to use `PreparedStatement`.”  
> “Inject API key using `@Value` from config.”

> ✅ **Expected Output:**
```java
@Value("${app.api.key}")
private String apiKey;

@GetMapping("/user")
public ResponseEntity<String> getUser(@RequestParam String email) throws SQLException {
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
    stmt.setString(1, email);
    ResultSet rs = stmt.executeQuery();
    return rs.next() ? ResponseEntity.ok("User Found") : ResponseEntity.notFound().build();
}
```

---
## ✅ Step 6: Add dependencies to POM.XML

> **Prompt:** \
> Open the `pom.xml` file  
> Add the required dependencies to support:  
> - Spring Web (`spring-boot-starter-web`)  
> - Spring Data JPA (`spring-boot-starter-data-jpa`)  
> - H2 in-memory database (`h2`)  
>  
> These are required to fix compilation errors related to:  
> - `jakarta.persistence` (for `@Entity`, `@Id`)  
> - `JpaRepository`  
>  
> Format the output as a single XML block (to copy into `<dependencies>`).


```xml
<!-- ...existing code... -->
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- H2 Database (for local testing) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- Jakarta Persistence API -->
    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
    </dependency>
    <!-- ...other dependencies... -->
</dependencies>
<!-- ...existing code... -->
```

---

## ✅ Step 7: Generate Documentation with Copilot

> **Prompt:**  
> Generate Javadoc for `UserController` methods:
> - Describe the parameters, return types, and exceptions.

> ✅ **Expected Output:**
```java
/**
 * Retrieves a user by email.
 * @param email The email address to search for.
 * @return 200 OK with user info if found, otherwise 404.
 * @throws SQLException if the DB connection fails.
 */
```

---

## ✅ Step 8: Create INSTRUCTIONS.md and CONTRIBUTING.md

> **Prompt 1 (INSTRUCTIONS.md):**  
> Create new `INSTRUCTIONS.md` in the UserApp directory \
> Provide instructions on setup, how to run, API endpoints, and technologies


> **Prompt 2 (CONTRIBUTING.md):** \
> Create new `CONTRIBUTING.md` in the UserApp directory \
> Provide a guide for this Spring Boot app using Maven.

---

## ✅ Step 9: Add GitHub Actions CI/CD

> **Prompt:**  
> Create the file user-ci.yaml in github root direction .github/workflows
> Generate GitHub Actions workflow  for:
> - Java 21
> - `mvn clean install` on push and pull request”
> - Workflow triggers on changes 

> ✅ **Expected Output (`.github/workflows/user-ci.yml`):**
```yaml
name: Java CI

on:
  push:
    paths:
      - 'session2/java/**'
    branches: [main]

jobs:
  build:
    name: Build and Test User App
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build with Maven
        run: mvn clean package
        working-directory: UserApp
```

---

## ✅ Step 10: Enable Secret Scanning and Push Protection


### ⚙️ Enable security features in GitHub:

1. Go to your repository on GitHub.
2. Click on **Settings** (top right of the repo page).
3. In the left sidebar, click **Code security**.
4. Enable the following options:
   - **Secret scanning** 🕵️‍♂️
   - **Push protection** 🚦
   - **Dependency graph** 📊
5. Click **Save** 💾 if

These steps will enable secret scanning, push protection, and the dependency graph for

---

## ✅ Step 11: Add CodeQL Scan 

### 🛠️ Configure CodeQL Workflow (`.github/workflows/codeql.yml`)
- Automated security analysis for Java code
- Runs on push/PR 
- Uses built-in and extended security queries

### ⚡ Key Steps
1. Checkout code
2. Setup JDK 21
3. Enable debug logging
4. Initialize CodeQL
5. Build Java code
6. Run security analysis

> **Prompt: Create CodeQL Workflow**  
> Create the file `.github/workflows/codeql.yaml` with the following configuration to analyze Java code securely:  
> - Use CodeQL v3 to scan Java code  
> - Trigger analysis on `push` and `pull_request`   
> - Set up JDK 21 using `temurin` with Maven caching enabled  
> - Enable debug logging and root cause flags for troubleshooting  
> - Use CodeQL query suites: `security-extended` and `security-and-quality`  
> - Perform `mvn -B clean compile` inside `UserApp`  
> - Configure permissions correctly for CodeQL uploads  
> - Ensure analysis runs on `ubuntu-latest`  
> - Include all required steps: checkout, JDK setup, debug mode, CodeQL init, Maven build, CodeQL analyze  

> ✅ **Expected Output (`.github/workflows/codeql.yaml`):**
```yaml
name: CodeQL

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

permissions:
  security-events: write
  contents: read

jobs:
  analyze:
    name: CodeQL Analyze Java
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Enable Debug Mode
        run: |
          echo "ACTIONS_STEP_DEBUG=true" >> $GITHUB_ENV
          echo "CODEQL_EXTRACTOR_JAVA_ROOT_CAUSE_ANALYSIS=true" >> $GITHUB_ENV

      - name: Initialize CodeQL
        uses: github/codeql-action/init@v3
        with:
          languages: java
          config-file: .github/codeql/config.yml

      - name: Build with Maven
        run: |
          cd session2/java/UserApp
          mvn -B clean compile --no-transfer-progress

      - name: Perform CodeQL Analysis
        uses: github/codeql-action/analyze@v3
        with:
          category: "/language:java"
          output: results

      - name: Debug Info
        run: |
          echo "Contents of .github/codeql:"
          ls -R .github/codeql/
          echo "Maven build directory:"
          ls -R UserApp/target/

      - name: Upload SARIF
        uses: actions/upload-artifact@v4
        with:
          name: codeql-results
          path: results/java.sarif
          retention-days: 5
```

### 🔧 Local CodeQL Security Test

```bash
# Create directory for CodeQL
mkdir -p ~/codeql-home
cd ~/codeql-home

# Download latest release
wget https://github.com/github/codeql-cli-binaries/releases/latest/download/codeql-linux64.zip

# Unzip the package
unzip codeql-linux64.zip

# Add to .bashrc
echo 'export PATH=$PATH:~/codeql-home/codeql' >> ~/.bashrc
source ~/.bashrc

# Check CodeQL version
codeql --version

# Test CLI works
codeql resolve languages

# Download standard query bundle
codeql pack download codeql/java-queries

# Initialize workspace
codeql pack init
```

```bash
# Create new database
codeql database create db \
  --language=java \
  --command "mvn clean compile" \
  --source-root session2/java/UserApp \
  --overwrite
```

```bash
# Navigate to CodeQL config directory
cd /workspaces/ghcp-course/.github/codeql

# Install dependencies
codeql pack install

```bash
# Navigate to CodeQL config directory
cd /workspaces/ghcp-course/.github/codeql

# Install dependencies
codeql pack install
```

```bash
# Install Java analysis pack
codeql pack download codeql/java-all

# Install security queries
codeql pack download codeql/java-queries
```

```bash
# Navigate back to project root
cd /workspaces/ghcp-course

# Run the query
codeql query run .github/codeql/queries/FindHardcodedSecrets.ql --database=db
```

---

## Step ✅ 12: Create a Custom CodeQL Query to Detect Hardcoded Secrets
Build and use a custom CodeQL query in a Java project to detect hardcoded secrets using GitHub Copilot and Actions.

### 📁 Create the Folder Structure

> **Prompt:** \
> Create the following directory structure for a custom CodeQL Java query pack:  
> `.github/codeql/queries`  
> Inside codeql, create:
> - `qlpack.yml`  
> - `config.yml`
> - A `queries/` folder containing `FindHardcodedSecrets.ql`  


> ✅ Expected Output:


✅ **Expected Output:**

```bash
# Create directories
mkdir -p .github/codeql/queries

# Create configuration files
touch .github/codeql/config.yml
touch .github/codeql/qlpack.yml
touch .github/codeql/queries/FindHardcodedSecrets.ql
```

> ✅ **Expected Output:**

```
.github/
├── codeql
│   ├── config.yml
│   ├── qlpack.yml
│   └── queries
│       └── FindHardcodedSecrets.ql
```

> `config.yml` -> Configures CodeQL analysis settings, paths, and query selection
```yaml 
name: "CodeQL Config"

disable-default-queries: false

queries:
  - uses: security-and-quality
  - uses: security-extended
  - uses: .
    from: userapp/secrets

paths:
  - 'UserApp/src/main/java'

paths-ignore:
  - '**/test/**'
  - '**/generated/**'
  - '**/target/**'

query-filters:
  - exclude:
      tags contain: test
```

> `qlpack.yml` -> Defines query pack metadata and dependencies for custom queries
```yaml
name: userapp/secrets
version: 0.0.1
dependencies:
  codeql/java-all: "*"
```

### ✨ Create `FindHardcodedSecrets.ql`
> **Prompt: Create Custom CodeQL Query – Detect Hardcoded Secrets**  
> Create a custom CodeQL query in `FindHardcodedSecrets.ql` to:
> - Detect hardcoded secrets in Java source code
> - Match field names containing sensitive keywords (`api_key`, `token`, `secret`, `password`)
> - Find values matching common secret patterns (`sk_*`, `apikey_*`, `token_*`, base64)
> - Report detected secrets with their actual values
> - Include standard CodeQL metadata
> - Ensure the query matches all field initializations with sensitive names and secret-like values
> - Fix any missing parentheses or syntax issues
> - Report detected secrets with their actual values

> ✅ **Expected Output:**

```ql
/**
 * @name Find hardcoded secrets
 * @description Detects hardcoded secrets in code
 * @kind problem
 * @problem.severity warning
 * @security-severity 8.0
 * @id java/hardcoded-secrets
 * @tags security
 */

import java

from StringLiteral literal
where
  exists(Field field |
    // Match field name patterns (case-insensitive)
    field.getName().regexpMatch("(?i).*(api_?key|token|secret|password).*") and
    // Match field initialization - this links the literal to the field
    literal = field.getInitializer() and
    // Match common secret patterns in the literal's value
    literal.getValue().regexpMatch("(?i).*(sk_.*|apikey_.*|token_.*|[a-zA-Z0-9+/=]{32,})")
  )
select
  literal,
  "Hardcoded secret detected: '" + literal.getValue() + "'"
```

### 📝 Add Test Case for Secret Detection
> **Prompt: \
> Provide Test cases for the ql script in User.java model
> API_KEY, String_TOKEN, SECRET, ENCODED, DESCRIPTION
```java
package com.example.UserApp.model;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;

    // ✅ Test case: API key pattern
    private static final String API_KEY = "sk_test_abc123";

    // ✅ Test case: Token pattern
    private static final String TOKEN = "token_1234567890abcdef";

    // ✅ Test case: Secret pattern
    private static final String SECRET = "apikey_secretvalue";

    // ✅ Test case: Password pattern
    private static final String PASSWORD = "myS3cretP@ssw0rd";

    // ✅ Test case: Base64-like string
    private static final String ENCODED = "dGhpcyBpcyBhIHZlcnlMb25nU3RyaW5nQmFzZTY0";

    // ❌ Should NOT match (not a sensitive field name)
    private static final String DESCRIPTION = "This is a regular description.";

  // getters/setters...

}

```

### 🚦 Push the change and trigger the workflow

Push this test file and monitor GitHub Actions CodeQL workflow logs for detected secrets

> ✅ **Expected Output:**

```yaml
Potential hardcoded secret found: sk_test_abc123
```

> 🧠 **NOTE: Viewing CodeQL Analysis Results**
>
> On **GitHub Pro (Public Repos)**:
> - ✅ Results can only be viewed in **GitHub Actions CI logs**
> - ❌ No access to the **Security** tab for CodeQL scan results
>
> On **GitHub Enterprise (GHAS)**:
> 1. **Accessing the Security Tab**
>    - Navigate to your repository  
>    - Click the **"Security"** tab  
>    - Select **"Code scanning"** from the left sidebar  
>    - View detailed **CodeQL analysis results**
>
> 2. **Available Features**
>    - Full analysis visualization  
>    - Custom query results  
>    - Interactive code navigation  
>    - Vulnerability tracking  
>    - Pull Request (PR) integration  
>    - Historical trend analysis  
>
> 3. **Key Locations**
>    - `Security` → `Code scanning alerts`  
>    - `Security` → `Code scanning analyses`  
>    - Pull Request checks with inline annotations  
>    - Security Overview dashboard



---

## ✅ Step 13: Add Dependabot
> **Prompt:**
> Create a `.github/dependabot.yml` file with:
> - `version: 2`
> - Enable updates for `maven` packages
> - Set directory to `UserApp` (where `pom.xml` resides)
> - Schedule updates to run **daily**
> - Add dependencies and automerge labels
> - Limit number of open PRs
> - Configure PR title format

> ✅ **Expected Output (`.github/dependabot.yml`):**
```yaml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "UserApp"
    schedule:
      interval: "daily"
    labels:
      - "dependencies"
      - "automerge"
    open-pull-requests-limit: 10
    pull-request-branch-name:
      separator: "-"
    commit-message:
      prefix: "📦 deps:"
```

---

## ✅ Step 14: Enforce Org-Wide CodeQL Policy *(Enterprise Only)*

> **Prompt:**  
> Inside your organization’s `.github` repository (e.g. `github.com/your-org/.github`), create a file named `.github/codeql/org-policy.yml`.  
> Define a policy to enforce a custom CodeQL query (`FindHardcodedSecrets.ql`) across all Java repositories.  
> The policy should:
> - Include the query pack from a central location
> - Target Java projects
> - Block commits that violate the rule

> ✅ **Expected Output (`.github/codeql/org-policy.yml`):**
```yaml
name: "Organization Security Policy"
disable-default-queries: false

# Query configuration
queries:
  - uses: org/codeql-queries@v1.0.0/java/security/FindHardcodedSecrets.ql
  - uses: security-and-quality

# Language settings
languages: 
  - java

# Path filters
paths:
  - '**/*.java'
paths-ignore:
  - '**/test/**'
  - '**/generated/**'

# Enforcement rules
rules:
  - id: java/hardcoded-secrets
    severity: error
    paths:
      - '**/*.java'
    mode: block
    message: |
      Hardcoded secrets detected. Please:
      1. Remove hardcoded credentials
      2. Use GitHub Secrets instead
      3. Update configuration to use environment variables
```

> 🛡️ **NOTE: Purpose of `Organization Security Policy`**
>
> This CodeQL configuration (`codeql-config.yml`) enforces a **centralized security scanning policy** across the organization:
>
> - **🔍 Custom Query Usage**  
>   Integrates a custom query `FindHardcodedSecrets.ql` from an internal org pack (`org/codeql-queries@v1.0.0`), along with the default `security-and-quality` suite.
>
> - **🗂️ Scope Definition**  
>   Restricts scanning to `.java` source files only, excluding test and generated code paths.
>
> - **🚫 Policy Enforcement**  
>   Applies a **blocking rule** for the `java/hardcoded-secrets` query:
>   - Marks any violations as `error`
>   - Prevents merges unless hardcoded secrets are removed
>
> - **📣 Developer Guidance**  
>   Displays a message with instructions to:
>   1. Remove hardcoded credentials  
>   2. Use **GitHub Secrets** for sensitive values  
>   3. Configure app to use **environment variables**
>
> 🔁 This policy ensures all Java code in the org complies with security best practices by enforcing strict scanning and remediation before code is merged.

---

## ✅ Step 15: Use GitHub Security Graph API *(Enterprise Only)*

> **Prompt:**  
> Use GitHub’s Security GraphQL API to retrieve open vulnerability alerts from the `UserApp` repository.  
> Include:  
> - `vulnerableManifestFilename`  
> - Package name  
> - Severity  
> - Advisory description

> ✅ **Expected Output (GraphQL Query):**
```graphql
query VulnerabilityAlerts {
  repository(owner: "YOUR_USERNAME", name: "ghcp-course") {
    vulnerabilityAlerts(first: 100, states: OPEN) {
      nodes {
        vulnerableManifestPath
        securityVulnerability {
          package {
            name
          }
          severity
          advisory {
            description
          }
        }
      }
    }
  }
}
```

---

## 🔍 Query GitHub Security Alerts via GraphQL (query Step 15) *(Enterprise Only)*

You can retrieve vulnerability alerts (e.g. from CodeQL, Dependabot) using GitHub’s GraphQL API in two main ways:

### ✅ OptionA: Use GitHub GraphQL Explorer (Manual)

1. Open: [GitHub GraphQL Explorer](https://docs.github.com/en/graphql/overview/explorer)
2. Sign in with an account that has access to the repository
3. Paste the query in left panel
4. Click the "Play" button (▶️)
5. View results in right panel

> 🧠 **NOTE: Required Setup for Security Features**
>
> ### 🔑 Repository Configuration
>
> 1. **Repository Settings**
>    - Replace `YOUR_USERNAME` with your actual GitHub username
>    - Replace `ghcp-course` with your repository name
>
> 2. **Token Permissions**
>    - `read:security_events` scope
>    - `repo` scope (required for private repositories)
>    - `security_events` scope
>
> 3. **Access Requirements**
>    | Repository Type | Required Access |
>    |------------------|------------------|
>    | Public           | Read access      |
>    | Private          | Write access     |
>    | Security-related | Admin access     |
>
> 4. **Feature Enablement**
>    - Navigate to your repository’s **Settings**
>    - Go to **Security** → **Code scanning**
>    - Enable **Dependabot alerts**
>    - Enable **Security updates**



### 🛠️ Option B: Use Curl in Terminal or Script

Use this if you want to automate the query or run it from CI tools:

```bash
# Set your token
export GITHUB_TOKEN="your_token"

# Run query
curl -H "Authorization: bearer $GITHUB_TOKEN" \
     -H "Content-Type: application/json" \
     -X POST https://api.github.com/graphql \
     -d '{"query":"query { repository(owner: \"YOUR_USERNAME\", name: \"ghcp-course\") { vulnerabilityAlerts(first: 100, states: OPEN) { nodes { vulnerableManifestPath securityVulnerability { package { name } severity advisory { description } } } } } }"}'
```

> ✅ **Expected Output (GraphQL Query):**
```json
{
  "data": {
    "repository": {
      "vulnerabilityAlerts": {
        "nodes": [
          {
            "securityVulnerability": {
              "package": {
                "name": "org.springframework.boot"
              },
              "severity": "HIGH",
              "advisory": {
                "description": "Vulnerability in Spring Boot..."
              }
            },
            "vulnerableManifestPath": "UserApp/pom.xml"
          }
        ]
      }
    }
  }
}
```

---

## 🧩 Summary: GHCP Instructor Demo – `UserApp` with Copilot, CodeQL, and GitHub Security

| Step | Task                              | Description                                                                                 |
|------|-----------------------------------|---------------------------------------------------------------------------------------------|
| 1    | Scaffold Project                  | Generate a Spring Boot app with Java 21, Spring Web, JPA, and H2 using Spring Initializr.   |
| 2    | Define Architecture               | Implement `User`, `UserRepository`, `UserService`, and `UserController` components.         |
| 3    | Add Configuration                 | Set up `application.properties` with API key, H2 DB config, and Hibernate settings.         |
| 4    | Add Vulnerable Code               | Simulate poor practices with hardcoded secrets and unsafe SQL via `Statement`.              |
| 5    | Refactor with Copilot             | Use GitHub Copilot to fix vulnerabilities using `@Value` and `PreparedStatement`.           |
| 6    | Add Dependencies                  | Define required dependencies (Spring Web, JPA, H2) in `pom.xml`.                            |
| 7    | Generate Javadoc                  | Use Copilot to auto-generate method-level documentation for the controller.                 |
| 8    | Add Documentation Files           | Create `INSTRUCTIONS.md` and `CONTRIBUTING.md` with setup and usage guidelines.             |
| 9    | Configure CI Workflow             | Add `user-ci.yml` to run Maven build/test on push and pull request events.                  |
| 10   | Enable GitHub Security Features   | Turn on Secret Scanning, Push Protection, and Dependency Graph in repository settings.      |
| 11   | Add CodeQL Scan                   | Configure `codeql.yml` workflow with `security-extended` rules for Java.                    |
| 12   | Add Custom CodeQL Query           | Write `FindHardcodedSecrets.ql` to detect embedded secrets via regex.                       |
| 13   | Enable Dependabot Updates         | Use `dependabot.yml` to automate Maven dependency updates with custom settings.             |
| 14   | Enforce Org-Wide CodeQL Policy    | (GHES) Add `org-policy.yml` to block hardcoded secrets across all Java repos.               |
| 15   | Query Security Alerts via GraphQL | (GHES) Use GitHub GraphQL API to fetch vulnerability alerts and advisories.                 |


---

#### 🧑‍🏫 Author: Georges Bou Ghantous  
<sub><i>This repository demonstrates secure coding practices for Spring Boot applications using GitHub Copilot, CodeQL, Dependabot, and GitHub Advanced Security — with real-world workflows for layered architecture, vulnerability scanning, and automated CI/CD.</i></sub>


