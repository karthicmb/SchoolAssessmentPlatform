package com.karthic.schoolassessment.controller;

import com.karthic.schoolassessment.dto.QuestionDTO;
import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.ExamResult;
import com.karthic.schoolassessment.model.Question;
import com.karthic.schoolassessment.service.ExamService;
import com.karthic.schoolassessment.service.ResultService;
import com.karthic.schoolassessment.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/student/exam")
public class ExamController {

    private static final Logger log = LoggerFactory.getLogger(ExamController.class);

    private final ExamService examService;
    private final SessionService sessionService;
    private final ResultService resultService;
    private final int questionsPerPage;

    public ExamController(
            ExamService examService,
            SessionService sessionService,
            ResultService resultService,
            @Value("${questions.per.page}") int questionsPerPage) {
        this.examService = examService;
        this.sessionService = sessionService;
        this.resultService = resultService;
        this.questionsPerPage = questionsPerPage;
    }

    @GetMapping("/{examId}/start")
    public String startExam(
            @PathVariable String examId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) throws IOException {
        ExamMetadata metadata = examService.getExam(examId);
        sessionService.startExam(session, examId, metadata.getQuestionCount());
        log.info("Exam started: examId={}, student={}", examId, userDetails.getUsername());
        return "redirect:/student/exam/" + examId + "?page=1";
    }

    @GetMapping("/{examId}")
    public String showPage(
            @PathVariable String examId,
            @RequestParam(defaultValue = "1") int page,
            HttpSession session,
            Model model) throws IOException {

        ExamMetadata metadata = examService.getExam(examId);

        if (!sessionService.isExamActive(session) || !examId.equals(sessionService.getExamId(session))) {
            return "redirect:/student/exam/" + examId + "/start";
        }

        int totalPages = (int) Math.ceil((double) metadata.getQuestionCount() / questionsPerPage);
        page = Math.max(1, Math.min(page, totalPages));

        List<Question> pageQuestions = examService.loadQuestionsPage(examId, page, questionsPerPage);
        Map<Integer, String> answers = sessionService.getAnswers(session);

        List<QuestionDTO> questionDTOs = pageQuestions.stream()
                .map(q -> new QuestionDTO(q.number(), q.question(), q.option1(), q.option2(), q.option3(), q.option4()))
                .toList();

        List<Integer> allNumbers = IntStream.rangeClosed(1, metadata.getQuestionCount())
                .boxed().toList();

        model.addAttribute("exam", metadata);
        model.addAttribute("examId", examId);
        model.addAttribute("questions", questionDTOs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("answers", answers);
        model.addAttribute("answeredCount", answers.size());
        model.addAttribute("totalQuestions", metadata.getQuestionCount());
        model.addAttribute("allNumbers", allNumbers);
        model.addAttribute("questionsPerPage", questionsPerPage);
        model.addAttribute("answeredList", new ArrayList<>(answers.keySet()));

        if (metadata.isTimerEnabled()) {
            long elapsed = (System.currentTimeMillis() - sessionService.getStartTimeMillis(session)) / 1000;
            long remaining = (long) metadata.getDuration() * 60 - elapsed;
            model.addAttribute("timerEnabled", true);
            model.addAttribute("remainingSeconds", Math.max(0, remaining));
        } else {
            model.addAttribute("timerEnabled", false);
            model.addAttribute("remainingSeconds", 0L);
        }

        return "student/exam";
    }

    @PostMapping("/{examId}")
    public String navigate(
            @PathVariable String examId,
            @RequestParam int currentPage,
            @RequestParam String action,
            @RequestParam Map<String, String> allParams,
            HttpSession session) throws IOException {

        savePageAnswers(session, allParams);

        if ("submit".equals(action)) {
            Map<Integer, String> answers   = sessionService.getAnswers(session);
            ExamMetadata metadata          = examService.getExam(examId);
            List<Question> questions       = examService.loadQuestions(examId);
            ExamResult result              = resultService.evaluate(metadata, questions, answers);
            resultService.saveResult(result);
            sessionService.clearExam(session);
            log.info("Exam submitted: examId={}, resultId={}", examId, result.getResultId());
            return "redirect:/student/result/" + result.getResultId();
        }
        if ("prev".equals(action)) {
            return "redirect:/student/exam/" + examId + "?page=" + Math.max(1, currentPage - 1);
        }
        if ("next".equals(action)) {
            int totalPages = (int) Math.ceil(
                    (double) sessionService.getTotalQuestions(session) / questionsPerPage);
            return "redirect:/student/exam/" + examId + "?page=" + Math.min(totalPages, currentPage + 1);
        }
        if (action.startsWith("page-")) {
            int targetPage = Integer.parseInt(action.substring(5));
            return "redirect:/student/exam/" + examId + "?page=" + targetPage;
        }

        return "redirect:/student/exam/" + examId + "?page=" + currentPage;
    }

    @GetMapping("/{examId}/review/{resultId}")
    public String reviewAnswers(
            @PathVariable String examId,
            @PathVariable String resultId,
            Model model) throws IOException {
        model.addAttribute("result", resultService.getResult(resultId));
        model.addAttribute("questions", examService.loadQuestions(examId));
        return "student/review";
    }

    @PostMapping("/{examId}/clear-answer")
    @ResponseBody
    public Map<String, String> clearAnswer(
            @PathVariable String examId,
            @RequestParam int questionNumber,
            HttpSession session) {
        sessionService.clearAnswer(session, questionNumber);
        log.debug("Cleared answer: examId={}, question={}", examId, questionNumber);
        return Map.of("status", "cleared");
    }

    @PostMapping("/{examId}/save-answer")
    @ResponseBody
    public Map<String, String> saveAnswer(
            @PathVariable String examId,
            @RequestParam int questionNumber,
            @RequestParam String answer,
            HttpSession session) {
        sessionService.saveAnswers(session, Map.of(questionNumber, answer));
        log.debug("Auto-saved: examId={}, question={}", examId, questionNumber);
        return Map.of("status", "saved");
    }

    private void savePageAnswers(HttpSession session, Map<String, String> params) {
        Map<Integer, String> pageAnswers = new java.util.HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("answer_")) {
                try {
                    int qNum = Integer.parseInt(key.substring(7));
                    pageAnswers.put(qNum, value);
                } catch (NumberFormatException ignored) {}
            }
        });
        sessionService.saveAnswers(session, pageAnswers);
    }
}
