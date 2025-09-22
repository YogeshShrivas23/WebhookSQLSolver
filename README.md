# WebhookSQLSolver

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-Build-orange.svg)

Spring Boot app for Bajaj Finserv Health Qualifier 1.

## Description
WebhookSQLSolver is a Spring Boot application that automates the workflow for Bajaj Finserv Health Qualifier 1. The app sends a webhook request, solves an SQL problem based on your registration number, and submits the solution automatically on startup—no manual trigger or REST endpoint required.

## Build & Run Instructions

1. **Install Java 17 and Maven**
   - Make sure Java 17 and Maven are installed and available in your PATH.
2. **Clone the repository**
   ```sh
   git clone <your-repo-url>
   cd WebhookSQLSolver
   ```
3. **Navigate to the project folder**
   ```sh
   cd WebhookSQLSolver
   ```
4. **Build the JAR**
   ```sh
   mvn clean package
   ```
   The JAR will be created in the `target/` folder as `WebhookSQLSolver-1.0.0.jar`.
5. **Run the JAR**
   ```sh
   java -jar target/WebhookSQLSolver-1.0.0.jar
   ```

## GitHub Submission Instructions
- After building, copy the JAR file to the `jar/` folder in your repository.
- Provide a raw downloadable link to the JAR, for example:
  ```
  https://github.com/<your-username>/<your-repo>/raw/main/jar/WebhookSQLSolver-1.0.0.jar
  ```
- Ensure the `jar/` folder is included in your commit.

## Notes
- The app runs the workflow automatically on startup—no REST endpoints or manual triggers required.
- For any issues, check your Java and Maven installation and ensure all dependencies are downloaded.
