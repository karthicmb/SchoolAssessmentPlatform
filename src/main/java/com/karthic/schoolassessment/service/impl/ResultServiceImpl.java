package com.karthic.schoolassessment.service.impl;

import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.ExamResult;
import com.karthic.schoolassessment.model.Question;
import com.karthic.schoolassessment.service.ResultService;
import com.karthic.schoolassessment.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResultServiceImpl implements ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StorageService storageService;
    private final Path resultPath;

    public ResultServiceImpl(StorageService storageService,
                              @Value("${storage.result-path}") String resultPath) {
        this.storageService = storageService;
        this.resultPath = Path.of(resultPath);
    }

    @Override
    public ExamResult evaluate(ExamMetadata metadata, List<Question> questions,
                                Map<Integer, String> studentAnswers) {
        Map<Integer, String> correctAnswers = new HashMap<>();
        for (Question q : questions) {
            correctAnswers.put(q.number(), q.correctAnswer());
        }

        int correct = 0;
        int wrong   = 0;
        int skipped = 0;

        for (Question q : questions) {
            String studentAnswer = studentAnswers.get(q.number());
            if (studentAnswer == null || studentAnswer.isBlank()) {
                skipped++;
            } else if (studentAnswer.equals(q.correctAnswer())) {
                correct++;
            } else {
                wrong++;
            }
        }

        double percentage        = metadata.getQuestionCount() > 0
                ? (correct * 100.0) / metadata.getQuestionCount() : 0.0;
        double roundedPercentage = Math.round(percentage * 10.0) / 10.0;

        String resultId = "result-" + metadata.getExamId() + "-" + System.currentTimeMillis();

        log.info("Result evaluated: examId={}, correct={}/{}, percentage={}%",
                metadata.getExamId(), correct, metadata.getQuestionCount(), roundedPercentage);

        return ExamResult.builder()
                .resultId(resultId)
                .examId(metadata.getExamId())
                .examTitle(metadata.getTitle())
                .subject(metadata.getSubject())
                .studentClass(metadata.getStudentClass())
                .totalQuestions(metadata.getQuestionCount())
                .correct(correct)
                .wrong(wrong)
                .skipped(skipped)
                .percentage(roundedPercentage)
                .grade(calculateGrade(percentage))
                .submittedAt(LocalDateTime.now().format(FORMATTER))
                .studentAnswers(new HashMap<>(studentAnswers))
                .correctAnswers(correctAnswers)
                .build();
    }

    @Override
    public void saveResult(ExamResult result) throws IOException {
        Path resultFile = resultPath.resolve(result.getResultId() + ".json");
        storageService.writeJson(resultFile, result);
        log.info("Result saved: resultId={}", result.getResultId());
    }

    @Override
    public List<ExamResult> listResults() throws IOException {
        if (!Files.exists(resultPath)) {
            return List.of();
        }
        List<ExamResult> results = new ArrayList<>();
        try (var stream = Files.list(resultPath)) {
            for (Path file : stream.filter(p -> p.toString().endsWith(".json")).toList()) {
                results.add(storageService.readJson(file, ExamResult.class));
            }
        }
        results.sort(Comparator.comparing(ExamResult::getSubmittedAt).reversed());
        return results;
    }

    @Override
    public ExamResult getResult(String resultId) throws IOException {
        Path resultFile = resultPath.resolve(resultId + ".json");
        return storageService.readJson(resultFile, ExamResult.class);
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }
}
