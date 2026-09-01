# Campus Management System

## Project Goal

This is a 7-day learning project.

The goal is to build a campus activity management system while learning practical Java backend development.

The developer is a Java beginner, so prioritize:
- readability
- simplicity
- understanding

Do not optimize for complex enterprise architecture.

---

# Technology Stack

Backend:

- Java 17
- Spring Boot
- Maven
- MyBatis-Plus
- MySQL 8

Frontend:

- Vue 3
- Vite
- Axios
- Element Plus

Database:

- MySQL
- Database name: campus_management

---

# Project Structure

Use this structure:

campus-management-system/

├── AGENTS.md

├── backend/

└── frontend/

---

# Backend Architecture

Use simple layered architecture:

Controller

↓

Service

↓

Mapper

↓

Database


Responsibilities:

Controller:
- Receive HTTP requests
- Return responses


Service:
- Handle business logic


Mapper:
- Database operations


Entity:
- Database objects


Do not put business logic in Controller.

---

# Development Rules

1. Keep the project simple.

2. Do not introduce unnecessary technologies.

Do NOT add:

- Redis
- Kafka
- Microservices
- Docker
- Kubernetes
- Spring Cloud

unless explicitly requested.

3. Avoid over-engineering.

4. Prefer beginner-friendly code.

5. Explain important code changes.

---

# Learning Mode

After every important modification:

Explain:

1. What files changed.
2. Why these files exist.
3. Important Java/Spring concepts.
4. How to test the feature.

The developer wants to learn, not only generate code.

---

# Day 1 Goal

# Current Learning Stage

Current project progress:

- Day 1 completed:
    - Spring Boot backend initialized
    - Vue frontend initialized
    - MySQL connected
    - GET /api/users completed
    - Vue successfully displays user list

Current focus:
- User management CRUD
- Keep architecture simple
- Do not introduce authentication or permission systems yet

# Day 2 Goal

Implement basic user management CRUD.

Required features:

- List users
- Create user
- Update user
- Delete user
- Search users by username or name

Do not implement yet:

- Login
- JWT
- Spring Security
- Role/permission management
- Pagination
- Redis
- Docker
- Vue Router
- Pinia

## Current Progress

Completed:
- Spring Boot backend initialized
- Vue frontend initialized
- MySQL connected
- User list implemented
- User create/update/delete implemented
- User search implemented
- User frontend CRUD and search implemented
- User management features have been manually tested

## Current Goal

Implement basic username/password login.

For the first login implementation:
- Use POST /api/login
- Accept username and password
- Query the user by username
- Verify the password
- Return a simple login success/failure response
- Keep the implementation simple and suitable for learning

Do NOT add yet:
- JWT
- Spring Security
- Role/permission system
- Refresh tokens
- Redis
- OAuth
- Complex authentication architecture

The basic login flow should be understood and tested first before adding JWT or authentication middleware.

## Development Constraints

- Keep the existing project structure and coding style where possible.
- Avoid unnecessary refactoring.
- Do not modify unrelated working features.
- User management CRUD and search must continue to work.
- Prefer simple implementations appropriate for a learning project.