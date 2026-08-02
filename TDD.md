# School Assessment Platform (SAP)

# Technical Design Document (TDD)

Version : 1.0

Author : Karthic Kumar

Technology

- Java 21
- Spring Boot 3.5
- Maven
- Thymeleaf
- Bootstrap 5
- Spring Security
- Apache Commons CSV

---

# 1. Architecture

The application follows a simple layered architecture.

```

Browser

↓

Controller

↓

Service

↓

Storage Service

↓

Local File System

```

No database.

No REST API.

No Repository layer.

No ORM.

Everything is stored as CSV or JSON.

---

# 2. Project Structure

```

SchoolAssessmentPlatform

│

├── src

│ ├── main

│ │

│ ├── java

│ │

│ └── com.karthic.schoolassessment

│ │

│ ├── config

│ ├── controller

│ ├── service

│ ├── service.impl

│ ├── storage

│ ├── model

│ ├── dto

│ ├── util

│ ├── security

│ ├── exception

│ └── constants

│

├── resources

│

├── templates

├── static

├── application.properties

│

├── data

│

├── exams

├── results

├── config

│

└── pom.xml

```

---

# 3. Package Responsibilities

## config

Contains Spring configuration.

Classes

```

SecurityConfig

WebConfig

ApplicationConfig

```

---

## controller

Contains MVC Controllers.

```

LoginController

DashboardController

AdminController

StudentController

ExamController

ResultController

SettingsController

```

Controllers only coordinate requests.

No business logic.

---

## service

Contains interfaces.

```

AuthenticationService

ExamService

CsvService

ResultService

StorageService

SettingsService

SessionService

TimerService

UserService

```

---

## service.impl

Contains implementation.

```

AuthenticationServiceImpl

ExamServiceImpl

CsvServiceImpl

ResultServiceImpl

StorageServiceImpl

SettingsServiceImpl

SessionServiceImpl

TimerServiceImpl

UserServiceImpl

```

---

## storage

Handles file access.

```

FileStorage

JsonStorage

CsvStorage

```

---

## model

```

Question

Exam

ExamMetadata

ExamResult

StudentAnswer

User

Settings

```

Prefer Java Records where suitable.

---

## dto

```

UploadExamRequest

ExamSummary

QuestionDTO

ResultDTO

LoginDTO

```

---

## util

```

CsvValidator

FileUtil

JsonUtil

DateUtil

PasswordUtil

```

---

## exception

```

CsvValidationException

ExamNotFoundException

StorageException

AuthenticationException

GlobalExceptionHandler

```

---

# 4. Local Storage

Everything stored under

```

data/

```

Structure

```

data/

config/

users.json

settings.json

exams/

exam-001/

questions.csv

exam.json

exam-002/

results/

student/

2026/

exam001-result.json

```

Application creates folders automatically.

---

# 5. application.properties

```

server.port=8080

spring.application.name=SchoolAssessmentPlatform

spring.thymeleaf.cache=false

storage.base-path=data

storage.exam-path=${storage.base-path}/exams

storage.result-path=${storage.base-path}/results

storage.config-path=${storage.base-path}/config

questions.per.page=10

default.exam.duration=60

session.timeout=30

```

Never hardcode paths.

---

# 6. Security Design

Spring Security.

Two users.

```

ROLE_ADMIN

ROLE_STUDENT

```

Use BCrypt.

In-memory authentication is acceptable for the first version, with users persisted to `users.json` for password changes if implemented later.

---

Protected URLs

```

/admin/**

ROLE_ADMIN

/student/**

ROLE_STUDENT

```

Common

```

/login

/logout

```

Session timeout

30 minutes.

---

# 7. Controller Design

## LoginController

Responsibilities

```

Show Login

Authenticate

Logout

```

---

## DashboardController

Responsibilities

```

Redirect according to role

```

---

## AdminController

Responsibilities

```

Upload Exam

Delete Exam

Manage Timer

List Exams

Settings

```

---

## StudentController

Responsibilities

```

Dashboard

Exam List

Result History

```

---

## ExamController

Responsibilities

```

Start Exam

Load Questions

Submit

Review

```

---

## ResultController

Responsibilities

```

View Result

Download (future)

History

```

---

# 8. Service Responsibilities

## CsvService

```

Read CSV

Validate CSV

Convert CSV

Return Questions

```

---

## ExamService

```

Create Exam

Delete Exam

Load Exam

Save Metadata

List Exams

```

---

## ResultService

```

Evaluate Answers

Generate Result

Save Result

Read History

```

---

## StorageService

```

Read JSON

Write JSON

Copy File

Delete Folder

```

---

## SessionService

```

Create Session

Save Answers

Restore Answers

Clear Session

```

---

## TimerService

```

Start Timer

Check Timeout

Calculate Remaining

```

---

# 9. CSV Validation

Required columns

```

Question

Option1

Option2

Option3

Option4

CorrectAnswer

```

Validation

Question mandatory.

Four options mandatory.

Correct answer must match one option exactly.

No empty rows.

Reject malformed CSV.

---

# 10. Exam Flow

```

Admin Uploads CSV

↓

CSV Validation

↓

Save CSV

↓

Create Metadata

↓

Display in Dashboard

↓

Student Starts Exam

↓

Load Questions

↓

Answer Questions

↓

Auto Save

↓

Submit

↓

Evaluation

↓

Result

↓

History

```

---

# 11. Pagination

Questions per page

```

10

```

Configurable.

Stored in application.properties.

Navigation

```

Previous

Next

Jump Page

```

---

# 12. Timer

Timer enabled per exam.

If enabled

Display countdown.

When timer expires

Auto submit.

Client-side countdown with server-side validation.

---

# 13. Result Calculation

Algorithm

```

For each question

Compare

Selected

vs

Correct

Count Correct

Count Wrong

Percentage

(correct / total) × 100

Grade

A 90+

B 80+

C 70+

D 60+

F below 60

```

---

# 14. Result Storage

Each attempt

One JSON.

```

student

↓

year

↓

result.json

```

---

# 15. Exception Handling

Use @ControllerAdvice.

Handle

```

CSV Errors

Storage Errors

Authentication

404

500

```

User-friendly error pages.

---

# 16. Logging

Use SLF4J.

Log

```

Login

Upload

Delete

Exam Start

Exam Submit

Result Generated

```

Do not log passwords.

---

# 17. UI Navigation

```

Login

↓

Dashboard

↓

Exam List

↓

Take Exam

↓

Submit

↓

Result

↓

Review

↓

Dashboard

```

---

# 18. Coding Standards

Java 21.

Constructor Injection only.

No field injection.

No static mutable variables.

Meaningful method names.

Maximum method length

40 lines.

Maximum class length

300 lines where practical.

Use records for immutable DTOs/models.

---

# 19. Testing

Minimum

Service Layer

CSV Validation

Result Calculation

Timer Logic

Storage

Smoke test the application manually after major changes.

---

# 20. Performance

100-question paper

Load

< 2 seconds

Result

< 2 seconds

Startup

< 5 seconds

---

# 21. Future Extension Points

Image questions

Random question order

Random option order

Multiple students

Export PDF

Excel upload

Subject analytics

Progress charts

Dark mode

---

End of Document