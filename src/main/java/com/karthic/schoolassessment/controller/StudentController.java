package com.karthic.schoolassessment.controller;

import com.karthic.schoolassessment.service.ExamService;
import com.karthic.schoolassessment.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/student")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final ExamService examService;
    private final ResultService resultService;

    public StudentController(ExamService examService, ResultService resultService) {
        this.examService = examService;
        this.resultService = resultService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Student dashboard accessed by: {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        return "student/dashboard";
    }

    @GetMapping("/exams")
    public String examList(@AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        log.info("Student exam list accessed by: {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("exams", examService.listExams());
        return "student/exams";
    }

    @GetMapping("/results")
    public String resultHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) throws IOException {
        log.info("Result history accessed by: {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("results", resultService.listResults());
        return "student/results";
    }

    @GetMapping("/result/{resultId}")
    public String viewResult(
            @PathVariable String resultId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) throws IOException {
        log.info("Result viewed: resultId={}, student={}", resultId, userDetails.getUsername());
        model.addAttribute("result", resultService.getResult(resultId));
        model.addAttribute("username", userDetails.getUsername());
        return "student/result";
    }
}
