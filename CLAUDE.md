# CLAUDE.md

# School Assessment Platform

## Purpose

This repository contains an offline web-based examination platform built for home use.

The application will be used by one parent (Admin) and one student.

It runs entirely on a Windows laptop using:

- Java 21
- Spring Boot 3.5
- Maven
- Thymeleaf
- Bootstrap 5

The application is **NOT** intended for cloud deployment, enterprise use, or multiple users.

Always prefer **simplicity** over flexibility.

---

# Project Philosophy

When implementing this project, always follow these principles.

1. Keep the code simple.
2. Keep the code readable.
3. Avoid unnecessary abstractions.
4. Avoid unnecessary design patterns.
5. Prefer maintainability over cleverness.
6. Use Spring Boot conventions whenever possible.
7. Minimize dependencies.
8. Avoid premature optimization.

If there are two possible implementations, choose the simpler one.

---

# Primary Goal

The application should allow:

Admin

- Login
- Upload CSV question papers
- Delete exams
- Configure timer
- View results

Student

- Login
- Take exams
- Submit exams
- Review answers
- View result history

Everything should work without an Internet connection.

---

# Technology Stack

Always use

- Java 21
- Spring Boot 3.5.x
- Maven
- Thymeleaf
- Bootstrap 5
- Spring Security
- Apache Commons CSV
- Jackson
- Lombok

Do not replace these technologies unless explicitly requested.

---

# Technologies NOT Allowed

Do NOT introduce:

- Database
- MySQL
- PostgreSQL
- MongoDB
- Redis
- Spring Data JPA
- Hibernate
- REST API
- GraphQL
- Docker
- Kubernetes
- Kafka
- RabbitMQ
- Microservices
- OAuth
- JWT
- Firebase
- AWS
- Azure
- Google Cloud
- React
- Angular
- Vue
- Node.js
- TypeScript

The application must remain a simple Spring MVC application.

---

# Architecture

Follow this architecture.

Browser

↓

Spring MVC Controller

↓

Service

↓

Storage Service

↓

Local File System

No Repository layer.

No ORM.

No Database.

---

# Storage

Everything must be stored locally.

Directory

data/

Subfolders

config/

exams/

results/

Application must automatically create folders if missing.

Never hardcode paths.

Read all paths from

application.properties

---

# CSV Upload

CSV files are uploaded ONLY by Admin.

Student never uploads files.

Required CSV format

Question,Option1,Option2,Option3,Option4,CorrectAnswer

Validate every upload.

Reject invalid CSV.

Display meaningful validation messages.

---

# Authentication

Use Spring Security.

Only two users exist.

ROLE_ADMIN

ROLE_STUDENT

Use BCrypt password encoding.

Use session-based authentication.

Do not use JWT.

---

# Coding Rules

Always

- Constructor Injection
- SLF4J Logging
- Records for immutable DTOs where appropriate
- Final fields where appropriate
- Meaningful method names
- Meaningful variable names
- Small methods
- Small classes
- JavaDoc only for public APIs that need explanation

Never

- Field Injection
- Static mutable variables
- Business logic inside Controllers
- Duplicate code
- Hardcoded strings where constants improve clarity
- Hardcoded file locations

---

# Controller Rules

Controllers should

- Validate request
- Call service
- Return Thymeleaf page

Controllers must NOT

- Parse CSV
- Read files
- Calculate score
- Write JSON
- Contain business logic

---

# Service Rules

Services contain all business logic.

Every major feature should have its own service.

Prefer interface + implementation when it adds clarity.

Do not create unnecessary services.

---

# Storage Rules

Use Java NIO.

Always use

Path

Files

StandardCopyOption

Never use old java.io APIs unless required.

---

# Exception Handling

Use

@ControllerAdvice

Create user-friendly error pages.

Never expose stack traces to users.

Log full exception details.

---

# Logging

Log

Application startup

Login success

Login failure

CSV upload

CSV validation failure

Exam started

Exam submitted

Result generated

Exam deleted

Do NOT log

Passwords

Correct answers

Sensitive user information

---

# UI Guidelines

Use Bootstrap 5.

Responsive layout.

Blue and white theme.

Keep pages clean.

Avoid unnecessary animations.

Prefer Bootstrap components over custom CSS.

Use Bootstrap Icons where useful.

---

# Exam Rules

Questions per page

10

Configurable in

application.properties

Each question

- Four options
- One correct answer
- Single selection only

---

# Timer Rules

Timer enabled per exam.

If timer disabled

Hide countdown.

If timer enabled

Display countdown.

Automatically submit when timer reaches zero.

---

# Result Rules

Calculate

Correct

Wrong

Percentage

Grade

Store result as JSON.

Allow review of answers.

---

# File Naming

Use descriptive names.

Examples

ExamService.java

CsvService.java

ResultService.java

Never use names like

Util2.java

Helper.java

Temp.java

Test1.java

---

# Package Naming

Use

com.karthic.schoolassessment

All package names lowercase.

---

# Configuration

Everything configurable from

application.properties

Examples

server.port

questions.per.page

storage.base-path

default.exam.duration

Never hardcode configuration values.

---

# Code Style

Follow standard Java formatting.

Maximum method size

Approximately 40 lines.

Avoid nested logic.

Prefer early return.

Keep cyclomatic complexity low.

---

# Testing

Focus testing on

CSV validation

Result calculation

Storage

Timer logic

Manual UI verification is acceptable.

No requirement for 100% coverage.

---

# Future Enhancements

Design the code so these can be added later.

- Images in questions
- Excel upload
- PDF export
- Random question order
- Random option order
- Multiple students
- Charts
- Subject statistics

Do NOT implement these now.

---

# Definition of Done

A feature is complete only if

✓ Compiles successfully

✓ No warnings

✓ Follows project architecture

✓ Uses Bootstrap UI

✓ Uses Thymeleaf

✓ Stores data locally

✓ No database introduced

✓ No unnecessary dependencies added

✓ Runs using

mvn spring-boot:run

and

java -jar

without modification.

---

# Final Rule

Whenever there is uncertainty,

choose the simplest implementation that satisfies the requirements.

This is a family application, not an enterprise system.

Keep it clean.

Keep it maintainable.

Keep it enjoyable to use.