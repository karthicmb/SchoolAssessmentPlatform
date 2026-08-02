package com.karthic.schoolassessment.service;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * Manages exam session state: answers, start time, and active exam tracking.
 */
public interface SessionService {

    void startExam(HttpSession session, String examId, int totalQuestions);

    void saveAnswers(HttpSession session, Map<Integer, String> answers);

    Map<Integer, String> getAnswers(HttpSession session);

    String getExamId(HttpSession session);

    long getStartTimeMillis(HttpSession session);

    int getTotalQuestions(HttpSession session);

    boolean isExamActive(HttpSession session);

    void clearAnswer(HttpSession session, int questionNumber);

    void clearExam(HttpSession session);
}
