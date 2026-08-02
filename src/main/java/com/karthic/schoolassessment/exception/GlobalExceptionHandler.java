package com.karthic.schoolassessment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.NoSuchFileException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMissingResource(NoResourceFoundException ex) {
        log.debug("Static resource not found: {}", ex.getResourcePath());
        return "error/404";
    }

    @ExceptionHandler({ExamNotFoundException.class, NoSuchFileException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("statusCode", 404);
        model.addAttribute("statusText", "Page Not Found");
        model.addAttribute("message", "The exam or resource you requested could not be found.");
        return "error/404";
    }

    @ExceptionHandler({StorageException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStorageError(StorageException ex, Model model) {
        log.error("Storage error: {}", ex.getMessage(), ex);
        model.addAttribute("statusCode", 500);
        model.addAttribute("statusText", "Storage Error");
        model.addAttribute("message", "A file storage error occurred. Please check the data directory.");
        return "error/500";
    }

    @ExceptionHandler({CsvValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCsvValidation(CsvValidationException ex, Model model) {
        log.warn("CSV validation error reached global handler: {}", ex.getMessage());
        model.addAttribute("statusCode", 400);
        model.addAttribute("statusText", "Invalid File");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("statusCode", 500);
        model.addAttribute("statusText", "Something Went Wrong");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        return "error/500";
    }
}
