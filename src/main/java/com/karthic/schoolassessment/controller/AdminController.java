package com.karthic.schoolassessment.controller;

import com.karthic.schoolassessment.dto.UploadExamRequest;
import com.karthic.schoolassessment.exception.CsvValidationException;
import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.Question;
import com.karthic.schoolassessment.service.ExamService;
import com.karthic.schoolassessment.service.ResultService;
import com.karthic.schoolassessment.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ExamService examService;
    private final ResultService resultService;
    private final SettingsService settingsService;

    public AdminController(ExamService examService, ResultService resultService, SettingsService settingsService) {
        this.examService = examService;
        this.resultService = resultService;
        this.settingsService = settingsService;
    }

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Admin dashboard accessed by: {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        return "admin/dashboard";
    }

    // -------------------------------------------------------------------------
    // Result History
    // -------------------------------------------------------------------------

    @GetMapping("/results")
    public String viewResults(@AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        log.info("Admin results accessed by: {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("results", resultService.listResults());
        return "admin/results";
    }

    // -------------------------------------------------------------------------
    // Upload Exam
    // -------------------------------------------------------------------------

    @GetMapping("/upload")
    public String uploadForm(@AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        UploadExamRequest uploadRequest = new UploadExamRequest();
        uploadRequest.setDuration(settingsService.loadSettings().getDefaultExamDuration());
        model.addAttribute("uploadRequest", uploadRequest);
        model.addAttribute("username", userDetails.getUsername());
        return "admin/upload";
    }

    @PostMapping("/upload")
    public String uploadExam(
            @ModelAttribute("uploadRequest") UploadExamRequest uploadRequest,
            @RequestParam("csvFile") MultipartFile csvFile,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        String validationError = validateUploadRequest(uploadRequest, csvFile);
        if (validationError != null) {
            model.addAttribute("errorMessage", validationError);
            return "admin/upload";
        }

        try {
            ExamMetadata metadata = examService.createExam(uploadRequest, csvFile);
            log.info("Exam '{}' uploaded by {}", metadata.getTitle(), userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Exam '" + metadata.getTitle() + "' uploaded successfully with "
                    + metadata.getQuestionCount() + " questions.");
            return "redirect:/admin/exams";

        } catch (CsvValidationException e) {
            log.warn("CSV validation failed: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/upload";

        } catch (IOException e) {
            log.error("Upload failed for user {}", userDetails.getUsername(), e);
            model.addAttribute("errorMessage", "Upload failed due to a server error. Please try again.");
            return "admin/upload";
        }
    }

    // -------------------------------------------------------------------------
    // Manage Exams
    // -------------------------------------------------------------------------

    @GetMapping("/exams")
    public String listExams(@AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        model.addAttribute("username", userDetails.getUsername());
        List<ExamMetadata> exams = examService.listExams();
        model.addAttribute("exams", exams);
        model.addAttribute("examCount", exams.size());
        return "admin/exams";
    }

    @GetMapping("/exams/{examId}")
    public String examDetail(
            @PathVariable String examId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) throws IOException {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("exam", examService.getExam(examId));
        model.addAttribute("questions", examService.loadQuestions(examId));
        return "admin/exam-detail";
    }

    @PostMapping("/exams/delete")
    public String deleteExam(
            @RequestParam String examId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            ExamMetadata exam = examService.getExam(examId);
            examService.deleteExam(examId);
            log.info("Exam '{}' deleted by {}", exam.getTitle(), userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Exam '" + exam.getTitle() + "' deleted successfully.");
        } catch (IOException e) {
            log.error("Failed to delete exam {}", examId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to delete exam. Please try again.");
        }
        return "redirect:/admin/exams";
    }

    @PostMapping("/exams/timer")
    public String updateTimer(
            @RequestParam String examId,
            @RequestParam(defaultValue = "false") boolean timerEnabled,
            @RequestParam(defaultValue = "60") int duration,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            examService.updateTimerSettings(examId, timerEnabled, duration);
            log.info("Timer updated for exam {} by {}", examId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Timer settings updated successfully.");
        } catch (IOException e) {
            log.error("Failed to update timer for exam {}", examId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update timer. Please try again.");
        }
        return "redirect:/admin/exams";
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String validateUploadRequest(UploadExamRequest request, MultipartFile csvFile) {
        if (csvFile == null || csvFile.isEmpty()) return "Please select a CSV file to upload.";
        if (request.getTitle() == null || request.getTitle().isBlank()) return "Exam title is required.";
        if (request.getSubject() == null || request.getSubject().isBlank()) return "Subject is required.";
        if (request.getStudentClass() == null || request.getStudentClass().isBlank()) return "Class is required.";
        if (request.isTimerEnabled() && request.getDuration() <= 0) return "Duration must be greater than 0.";
        return null;
    }
}
