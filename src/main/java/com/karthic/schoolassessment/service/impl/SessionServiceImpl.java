package com.karthic.schoolassessment.service.impl;

import com.karthic.schoolassessment.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SessionServiceImpl implements SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private static final String KEY_EXAM_ID         = "examId";
    private static final String KEY_ANSWERS         = "examAnswers";
    private static final String KEY_START_TIME      = "examStartTime";
    private static final String KEY_TOTAL_QUESTIONS = "examTotalQuestions";

    @Override
    public void startExam(HttpSession session, String examId, int totalQuestions) {
        session.setAttribute(KEY_EXAM_ID, examId);
        session.setAttribute(KEY_ANSWERS, new HashMap<Integer, String>());
        session.setAttribute(KEY_START_TIME, System.currentTimeMillis());
        session.setAttribute(KEY_TOTAL_QUESTIONS, totalQuestions);
        log.info("Exam session started: examId={}, questions={}", examId, totalQuestions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveAnswers(HttpSession session, Map<Integer, String> newAnswers) {
        Map<Integer, String> existing = getAnswers(session);
        existing.putAll(newAnswers);
        session.setAttribute(KEY_ANSWERS, existing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, String> getAnswers(HttpSession session) {
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute(KEY_ANSWERS);
        return answers != null ? new HashMap<>(answers) : new HashMap<>();
    }

    @Override
    public String getExamId(HttpSession session) {
        return (String) session.getAttribute(KEY_EXAM_ID);
    }

    @Override
    public long getStartTimeMillis(HttpSession session) {
        Long startTime = (Long) session.getAttribute(KEY_START_TIME);
        return startTime != null ? startTime : System.currentTimeMillis();
    }

    @Override
    public int getTotalQuestions(HttpSession session) {
        Integer total = (Integer) session.getAttribute(KEY_TOTAL_QUESTIONS);
        return total != null ? total : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clearAnswer(HttpSession session, int questionNumber) {
        Map<Integer, String> answers = getAnswers(session);
        answers.remove(questionNumber);
        session.setAttribute(KEY_ANSWERS, answers);
        log.debug("Cleared answer for question: {}", questionNumber);
    }

    @Override
    public boolean isExamActive(HttpSession session) {
        return session.getAttribute(KEY_EXAM_ID) != null;
    }

    @Override
    public void clearExam(HttpSession session) {
        session.removeAttribute(KEY_EXAM_ID);
        session.removeAttribute(KEY_ANSWERS);
        session.removeAttribute(KEY_START_TIME);
        session.removeAttribute(KEY_TOTAL_QUESTIONS);
        log.info("Exam session cleared");
    }
}
