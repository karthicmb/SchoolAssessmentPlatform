package com.karthic.schoolassessment.exception;

public class ExamNotFoundException extends RuntimeException {

    public ExamNotFoundException(String examId) {
        super("Exam not found: " + examId);
    }
}
