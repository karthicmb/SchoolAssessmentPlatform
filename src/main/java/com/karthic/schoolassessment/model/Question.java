package com.karthic.schoolassessment.model;

public record Question(
        int number,
        String question,
        String option1,
        String option2,
        String option3,
        String option4,
        String correctAnswer
) {}
