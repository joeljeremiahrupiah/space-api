# Co-Working Space Booking Platform - Showcase Prototype

This project is a prototype demonstration for a co-working space booking platform, built as part of a developer showcase challenge. It includes a backend REST API (Spring Boot) and a frontend SPA (Angular).

## Technology Stack (Summary)

*   **Backend:** Java 21, Spring Boot 3.4.5, Spring Data JPA, Spring Security, Gradle, PostgreSQL
*   **Database:** PostgreSQL

---

## Setup and Running Instructions

These instructions cover setting up and running the backend API and the frontend application locally.

### Prerequisites

Ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 21 or later.
*   **PostgreSQL Server:** A running instance.
*   **Git:** For cloning.
*   **Database Client:** pgAdmin, DBeaver.

---

### Part 1: Backend Setup (Spring Boot)

1.  **Clone Repository:** (If applicable)
    ```bash
    git clone <your-repository-url>
    cd <your-repository-folder>/coworking-booking-system # Navigate to backend folder
    ```

2.  **Database Configuration:**
    *   Connect to your PostgreSQL instance and create the database:
        ```sql
        CREATE DATABASE coworking_db;
        ```
    *   *(Optional)* Create a dedicated database user and grant privileges on `coworking_db`.
    *   Open `src/main/resources/application.properties`.
    *   Update the `spring.datasource.*` properties:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/coworking_db # Adjust if needed
        spring.datasource.username=YOUR_DB_USERNAME
        spring.datasource.password=YOUR_DB_PASSWORD
        ```
    *   Ensure `spring.jpa.hibernate.ddl-auto` is set appropriately (`update` or `create` for initial setup).

3.  **Application Configuration:**
    *   In `src/main/resources/application.properties`, update the JWT secret:
        ```properties
        # Replace with a strong, random key
        app.jwt.secret=YOUR_ULTRA_SECURE_RANDOM_JWT_SECRET_KEY_HERE
        app.jwt.expirationMs=86400000 # e.g., 24 hours
        ```
    *   Update Pesapal sandbox credentials (use placeholders if you don't have keys):
        ```properties
        pesapal.api.consumerKey=YOUR_SANDBOX_CONSUMER_KEY_OR_PLACEHOLDER
        pesapal.api.consumerSecret=YOUR_SANDBOX_CONSUMER_SECRET_OR_PLACEHOLDER
        # Ensure other pesapal.* properties exist if referenced by code
        pesapal.callback.baseUrl=http://placeholder.invalid # Needs ngrok URL for actual callback testing
        ```

4.  **Build & Run:**
    *   Open a terminal in the backend project root (`coworking-booking-system`).
    *   Build and Run the application:
        ```bash
        # Linux/macOS:
        ./gradlew bootRun
        # Windows:
        .\gradlew bootRun
        ```
    *   The backend API should start, typically on `http://localhost:8081`. Check console logs for successful startup and database schema creation/update messages.

---