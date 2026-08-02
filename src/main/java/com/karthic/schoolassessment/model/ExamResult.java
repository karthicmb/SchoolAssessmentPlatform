package com.karthic.schoolassessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

    private String resultId;
    private String examId;
    private String examTitle;
    private String subject;
    private String studentClass;
    private int totalQuestions;
    private int correct;
    private int wrong;
    private int skipped;
    private double percentage;
    private String grade;
    private String submittedAt;
    private Map<Integer, String> studentAnswers;
    private Map<Integer, String> correctAnswers;
}
