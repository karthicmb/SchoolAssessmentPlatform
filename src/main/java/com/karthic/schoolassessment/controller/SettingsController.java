package com.karthic.schoolassessment.controller;

import com.karthic.schoolassessment.model.AppSettings;
import com.karthic.schoolassessment.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public String showSettings(@AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("settings", settingsService.loadSettings());
        return "admin/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String targetUser,
            @RequestParam(required = false) String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        String validationError = validatePasswordChange(targetUser, currentPassword, newPassword, confirmPassword);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("passwordError", validationError);
            return "redirect:/admin/settings";
        }

        try {
            settingsService.changePassword(targetUser, newPassword);
            log.info("Password changed for user '{}' by admin '{}'", targetUser, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("passwordSuccess",
                    "Password for '" + targetUser + "' updated successfully.");
        } catch (IOException e) {
            log.error("Failed to change password for user '{}'", targetUser, e);
            redirectAttributes.addFlashAttribute("passwordError", "Failed to save password. Please try again.");
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/app-defaults")
    public String saveAppDefaults(
            @RequestParam int defaultExamDuration,
            @RequestParam int questionsPerPage,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (defaultExamDuration <= 0) {
            redirectAttributes.addFlashAttribute("settingsError", "Default exam duration must be greater than 0.");
            return "redirect:/admin/settings";
        }
        if (questionsPerPage <= 0) {
            redirectAttributes.addFlashAttribute("settingsError", "Questions per page must be greater than 0.");
            return "redirect:/admin/settings";
        }

        try {
            AppSettings settings = AppSettings.builder()
                    .defaultExamDuration(defaultExamDuration)
                    .questionsPerPage(questionsPerPage)
                    .build();
            settingsService.saveSettings(settings);
            log.info("App defaults saved by '{}'", userDetails.getUsername());
            redirectAttributes.addFlashAttribute("settingsSuccess", "Settings saved successfully.");
        } catch (IOException e) {
            log.error("Failed to save app defaults", e);
            redirectAttributes.addFlashAttribute("settingsError", "Failed to save settings. Please try again.");
        }
        return "redirect:/admin/settings";
    }

    private String validatePasswordChange(
            String targetUser, String currentPassword, String newPassword, String confirmPassword) {

        if (newPassword == null || newPassword.isBlank()) {
            return "New password is required.";
        }
        if (newPassword.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "New password and confirmation do not match.";
        }
        if ("admin".equals(targetUser)) {
            if (currentPassword == null || currentPassword.isBlank()) {
                return "Current password is required.";
            }
            if (!settingsService.verifyCurrentPassword("admin", currentPassword)) {
                return "Current password is incorrect.";
            }
        }
        return null;
    }
}
