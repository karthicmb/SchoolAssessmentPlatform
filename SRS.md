# School Assessment Platform (SAP)

# Software Requirements Specification (SRS)

Version: 1.0

Author: Karthic Kumar

Target Platform: Windows Laptop

Technology: Java 21 + Spring Boot 3.5

---

# 1. Introduction

## 1.1 Purpose

The School Assessment Platform is a lightweight web-based examination system designed for home use.

It allows a parent (Admin) to upload question papers in CSV format and allows a student to take online assessments through a web browser.

The application is intended to run entirely on a local Windows laptop without requiring any database, internet connection, or cloud services.

---

## 1.2 Objectives

The primary objectives are:

- Conduct practice examinations.
- Reduce paper usage.
- Automatically evaluate answers.
- Show instant results.
- Maintain previous exam history.
- Keep the application extremely simple.
- Allow future enhancements.

---

## 1.3 Scope

This application supports:

- One Administrator
- One Student
- Unlimited Exams
- Offline Usage
- Local Storage

The application does NOT support:

- Multiple students
- Online deployment
- Database
- Email
- Cloud storage
- Mobile App

---

# 2. Technology Stack

Backend

- Java 21
- Spring Boot 3.5
- Spring MVC
- Spring Security

Frontend

- Thymeleaf
- Bootstrap 5
- HTML5
- CSS3
- JavaScript

Libraries

- Apache Commons CSV
- Jackson
- Lombok

Storage

- CSV
- JSON

---

# 3. User Roles

There are only two roles.

## Admin

Responsibilities

- Login
- Upload Exam
- Delete Exam
- Enable Timer
- Disable Timer
- View Results
- Delete Results
- Change Password

---

## Student

Responsibilities

- Login
- View Available Exams
- Start Exam
- Answer Questions
- Submit Exam
- View Results
- Review Answers
- Change Password

---

# 4. Functional Requirements

---

## FR-1 Login

Description

Users must login before accessing the system.

Acceptance Criteria

- Username required
- Password required
- Invalid login displays message
- Session created after successful login
- Logout available

---

## FR-2 Upload Exam

Only Admin can upload exams.

CSV is uploaded using browser.

Required Information

- Exam Title
- Subject
- Class
- CSV File
- Timer Enabled
- Duration

Acceptance Criteria

- CSV validation performed
- Invalid CSV rejected
- Duplicate title rejected
- Success message displayed

---

## FR-3 Delete Exam

Admin can delete an exam.

Deleting an exam removes

- CSV
- Metadata
- Results associated with exam (optional confirmation)

Confirmation dialog required.

---

## FR-4 View Exams

Student sees available exams.

Information shown

- Title
- Subject
- Questions
- Timer
- Start Button

---

## FR-5 Start Exam

Student clicks Start.

System loads questions.

Exam session begins.

If timer enabled

Countdown starts.

---

## FR-6 Question Navigation

Questions displayed

10 per page.

Navigation

- Previous
- Next
- Page Numbers

Question palette displayed.

Answered

Green

Unanswered

Gray

Current

Blue

---

## FR-7 Answer Questions

Each question has

Question

Four options

Single selection

Only one answer allowed.

Changing answer replaces previous answer.

---

## FR-8 Auto Save

Whenever

- Next clicked
- Previous clicked
- Page changed

Current answers are automatically saved in session.

No Save button required.

---

## FR-9 Timer

Admin decides

Timer Enabled

or

Timer Disabled

If enabled

Display countdown.

When time reaches zero

System automatically submits exam.

---

## FR-10 Submit Exam

Student clicks Submit.

Confirmation dialog

Submit Exam?

YES

NO

If YES

Evaluation begins.

---

## FR-11 Result

System displays

Exam Title

Correct Answers

Wrong Answers

Percentage

Grade

Time Taken

Pass/Fail

---

## FR-12 Review Answers

Student can review

Question

Selected Answer

Correct Answer

Status

Correct

Wrong

---

## FR-13 Result History

Student

View previous exams.

Admin

View all results.

---

## FR-14 Change Password

Admin

Can change password.

Student

Can change password.

Passwords stored securely.

---

# 5. CSV Specification

CSV uploaded by Admin.

Required Header

Question,Option1,Option2,Option3,Option4,CorrectAnswer

Example

What is 2+2?,1,2,3,4,4

Capital of India?,Delhi,Mumbai,Chennai,Kolkata,Delhi

Rules

Question cannot be empty.

Four options mandatory.

CorrectAnswer must match one option.

---

# 6. Local Storage

Everything stored inside

data/

Structure

data/

    exams/

    results/

    config/

No database.

---

# 7. Exam Folder Structure

Each exam stored separately.

Example

data/

    exams/

        exam-001/

            questions.csv

            exam.json

Example Metadata

{
  "examId":"exam-001",
  "title":"Maths Unit Test",
  "subject":"Maths",
  "class":"6",
  "questions":100,
  "timerEnabled":true,
  "duration":60
}

---

# 8. Result Storage

Results stored as JSON.

Example

{
  "student":"student",
  "examId":"exam-001",
  "correct":92,
  "wrong":8,
  "percentage":92
}

---

# 9. User Interface

Theme

Professional

Blue

White

Responsive

Bootstrap 5

---

Login Page

Simple centered login.

---

Admin Dashboard

Cards

Upload Exam

Manage Exams

Results

Settings

Logout

---

Student Dashboard

Cards

Available Exams

Result History

Change Password

Logout

---

Exam Screen

Top

Title

Progress

Timer

Middle

Question

Options

Bottom

Previous

Next

Submit

Right

Question Palette

---

Result Screen

Score Card

Correct

Wrong

Percentage

Grade

Review Answers

Back to Dashboard

---

# 10. Validation

Login

Username mandatory.

Password mandatory.

Upload

CSV mandatory.

Title mandatory.

Duration positive.

Exam

Answer optional.

Student may skip questions.

---

# 11. Security Requirements

Only Admin uploads exams.

Only Admin deletes exams.

Student cannot access admin pages.

Passwords encrypted.

Session timeout

30 minutes.

CSRF enabled.

---

# 12. Performance Requirements

Application startup

Less than 5 seconds.

Exam load

Less than 2 seconds.

Upload

Less than 3 seconds.

Result generation

Less than 2 seconds.

---

# 13. Browser Support

Google Chrome

Microsoft Edge

Firefox

---

# 14. Error Messages

Examples

Invalid Username or Password.

CSV format invalid.

Exam not found.

Session expired.

Upload failed.

Unexpected error occurred.

---

# 15. Future Enhancements

Image Questions

Question Randomization

Option Randomization

Subject Filtering

PDF Export

Excel Import

Dark Mode

Progress Charts

Multiple Students

---

# 16. Non Functional Requirements

Simple.

Readable.

Maintainable.

No unnecessary frameworks.

No database.

Offline.

Portable.

---

# 17. Out of Scope

Mobile App

Cloud Deployment

Docker

Kubernetes

REST API

Microservices

OAuth

Google Login

Email

SMS

Notifications

---

# 18. Acceptance Criteria

The application is accepted when:

✓ Admin can login.

✓ Student can login.

✓ Admin uploads CSV.

✓ CSV validated.

✓ Student starts exam.

✓ Timer works.

✓ Pagination works.

✓ Auto save works.

✓ Submit works.

✓ Result generated.

✓ Review answers works.

✓ Result history works.

✓ Delete exam works.

✓ Application runs using

java -jar

without any external dependency.

---

End of Document