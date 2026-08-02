# README_AI.md

# AI Development Roadmap

This document instructs Claude Code how to build the School Assessment Platform.

Read this document completely before generating code.

Also read:

- SRS.md
- TDD.md
- CLAUDE.md

Follow those documents exactly.

---

# Goal

Build a complete offline examination platform.

The application must

- Compile successfully
- Run locally
- Require no database
- Require no cloud
- Store everything in local folders
- Be simple to understand
- Be easy to maintain

Do NOT skip phases.

Finish each phase before starting the next.

---

# Development Rules

After every phase

- Verify compilation
- Remove unused imports
- Remove duplicate code
- Keep project buildable
- Update README if necessary

Never leave partially implemented features.

---

# Phase 1

Project Initialization

Tasks

- Create Maven project
- Configure Spring Boot 3.5
- Configure Java 21
- Add dependencies
- Create package structure
- Create application.properties
- Create data folders automatically
- Verify application starts

Deliverable

Application starts successfully.

---

# Phase 2

Security

Tasks

- Configure Spring Security
- Configure BCrypt
- Create Login page
- Create Logout
- Create Admin role
- Create Student role
- Configure Session Management

Deliverable

Admin login works.

Student login works.

Role protection works.

---

# Phase 3

Dashboard

Tasks

Create

Admin Dashboard

Student Dashboard

Navigation

Bootstrap Layout

Responsive UI

Deliverable

Users reach correct dashboard after login.

---

# Phase 4

Exam Storage

Tasks

Create

Storage Service

Exam Metadata

JSON Reader

JSON Writer

Folder Creator

Deliverable

Application manages local folders correctly.

---

# Phase 5

CSV Upload

Tasks

Create

Upload Page

CSV Validation

CSV Parser

Metadata Generator

Error Messages

Deliverable

Admin uploads valid CSV successfully.

Invalid CSV rejected.

---

# Phase 6

Exam Management

Tasks

List Exams

Delete Exam

Exam Details

Timer Settings

Deliverable

Admin manages exams.

---

# Phase 7

Student Exam

Tasks

Load Exam

Display Questions

Display Options

Pagination

Question Palette

Navigation

Deliverable

Student can answer questions.

---

# Phase 8

Auto Save

Tasks

Store Answers

Restore Answers

Session Handling

Deliverable

Refreshing browser does not lose answers.

---

# Phase 9

Timer

Tasks

Countdown

Auto Submit

Remaining Time

Deliverable

Timer works correctly.

---

# Phase 10

Evaluation

Tasks

Calculate Score

Correct

Wrong

Percentage

Grade

Deliverable

Result generated correctly.

---

# Phase 11

Review

Tasks

Review Answers

Highlight Correct

Highlight Wrong

Deliverable

Student reviews completed exam.

---

# Phase 12

History

Tasks

Store Result

View Previous Results

Delete Results

Deliverable

History page works.

---

# Phase 13

Settings

Tasks

Change Password

Timer Defaults

Application Settings

Deliverable

Settings page complete.

---

# Phase 14

Error Handling

Tasks

Controller Advice

Custom Exceptions

Error Pages

Validation

Deliverable

User-friendly errors.

---

# Phase 15

UI Polish

Tasks

Bootstrap Cards

Icons

Responsive

Spacing

Progress Bar

Dialogs

Deliverable

Professional appearance.

---

# Phase 16

Testing

Tasks

CSV Validation Tests

Storage Tests

Score Tests

Timer Tests

Manual Verification

Deliverable

Major features verified.

---

# Phase 17

Documentation

Tasks

Update README

Installation

Usage

Screenshots Placeholder

Deliverable

Repository documentation complete.

---

# Phase 18

Final Review

Verify

No database

No REST API

No JPA

No Hibernate

No unnecessary dependencies

No duplicated code

No unused classes

All pages linked

Deliverable

Application ready.

---

# Directory Structure

The final project should resemble:

```
SchoolAssessmentPlatform/

src/

main/

java/

resources/

templates/

static/

data/

config/

users.json

settings.json

exams/

results/

README.md

README_AI.md

SRS.md

TDD.md

CLAUDE.md

pom.xml
```

---

# Coding Checklist

Every new class must

- Have one responsibility
- Use constructor injection
- Follow package structure
- Be readable
- Be documented when necessary

---

# Before Creating New Classes

Always ask

Can an existing class handle this?

Avoid unnecessary abstraction.

Avoid unnecessary interfaces.

Prefer simplicity.

---

# Before Adding Dependencies

Ask

Is Spring Boot already providing this functionality?

Avoid additional libraries unless they provide significant value.

---

# UI Checklist

Every page should

- Use Bootstrap 5
- Be responsive
- Have consistent spacing
- Display success/error messages clearly
- Include breadcrumb or page title where appropriate
- Match the same visual theme

---

# Build Checklist

Before considering the project complete

✓ Project compiles

✓ Application starts

✓ Login works

✓ Upload works

✓ Exam works

✓ Timer works

✓ Result works

✓ History works

✓ Delete works

✓ Logout works

✓ No console errors

✓ No stack traces shown to users

✓ Runs using

mvn spring-boot:run

and

java -jar

---

# Git Commit Strategy

Create small logical commits.

Examples

```
Initial Spring Boot project

Configure Spring Security

Implement Admin Dashboard

Add CSV Upload

Implement Exam Engine

Add Timer

Implement Result Calculation

Improve UI

Documentation Update
```

Avoid one massive commit.

---

# Final Instruction

This project is intended for a parent and one child.

Do not over-engineer the solution.

Favor readability over cleverness.

Favor simplicity over flexibility.

Build something that can be maintained by a single Java developer for many years.

Every implementation decision should support that goal.