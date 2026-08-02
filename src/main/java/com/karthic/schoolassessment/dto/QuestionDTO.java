package com.karthic.schoolassessment.dto;

public record QuestionDTO(
        int number,
        String question,
        String option1,
        String option2,
        String option3,
        String option4
) {}
