package com.karthic.schoolassessment.service;

import com.karthic.schoolassessment.dto.UploadExamRequest;
import com.karthic.schoolassessment.exception.CsvValidationException;
import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Manages exam lifecycle: create, list, retrieve, and delete exams.
 */
public interface ExamService {

    ExamMetadata createExam(UploadExamRequest request, MultipartFile csvFile) throws IOException, CsvValidationException;

    List<ExamMetadata> listExams() throws IOException;

    ExamMetadata getExam(String examId) throws IOException;

    List<Question> loadQuestions(String examId) throws IOException;

    List<Question> loadQuestionsPage(String examId, int page, int pageSize) throws IOException;

    void deleteExam(String examId) throws IOException;

    void updateTimerSettings(String examId, boolean timerEnabled, int duration) throws IOException;
}
