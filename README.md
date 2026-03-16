# Quiz App API

A Spring Boot REST API for creating, retrieving, and submitting quizzes.

## Features
- **Create Quiz**: `POST /quiz/create` - Generate a quiz with random questions by category.
- **Get Quiz**: `GET /quiz/{id}` - Retrieve a quiz with questions (ID, title, options only).
- **Submit Quiz**: `POST /quiz/submit/{id}` - Submit answers and get a score.

## Setup
1. **Prerequisites**:
    - Java 17+
    - PostgreSQL
    - Maven

2. **Database**:
    - Create a PostgreSQL database named `QuizAppData`.
    - Configure credentials in `application.properties` (see `application-example.properties`).

3. **Run**:
   ```bash
   mvn spring-boot:run