package com.karthic.schoolassessment.service.impl;

import com.karthic.schoolassessment.dto.UploadExamRequest;
import com.karthic.schoolassessment.exception.CsvValidationException;
import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.Question;
import com.karthic.schoolassessment.service.CsvService;
import com.karthic.schoolassessment.service.ExamService;
import com.karthic.schoolassessment.service.StorageService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StorageService storageService;
    private final CsvService csvService;
    private final String examPath;

    public ExamServiceImpl(
            StorageService storageService,
            CsvService csvService,
            @Value("${storage.exam-path}") String examPath) {
        this.storageService = storageService;
        this.csvService = csvService;
        this.examPath = examPath;
    }

    @Override
    public ExamMetadata createExam(UploadExamRequest request, MultipartFile csvFile)
            throws IOException, CsvValidationException {

        byte[] csvBytes = csvFile.getBytes();

        List<Question> questions = csvService.parseAndValidate(csvBytes);

        if (titleExists(request.getTitle())) {
            throw new CsvValidationException(
                    "An exam with title '" + request.getTitle() + "' already exists.");
        }

        String examId = generateExamId();
        Path examDir = Path.of(examPath, examId);
        storageService.createDirectories(examDir);

        Files.write(examDir.resolve("questions.csv"), csvBytes);

        ExamMetadata metadata = ExamMetadata.builder()
                .examId(examId)
                .title(request.getTitle().trim())
                .subject(request.getSubject().trim())
                .studentClass(request.getStudentClass().trim())
                .questionCount(questions.size())
                .timerEnabled(request.isTimerEnabled())
                .duration(request.getDuration())
                .createdAt(LocalDateTime.now().format(DATETIME_FORMAT))
                .build();

        storageService.writeJson(examDir.resolve("exam.json"), metadata);
        log.info("Exam created: examId={}, title='{}', questions={}",
                examId, metadata.getTitle(), questions.size());

        return metadata;
    }

    @Override
    public List<ExamMetadata> listExams() throws IOException {
        Path examsDir = Path.of(examPath);
        if (!Files.exists(examsDir)) {
            return List.of();
        }

        List<ExamMetadata> exams = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(examsDir, Files::isDirectory)) {
            for (Path examDir : stream) {
                Path metadataFile = examDir.resolve("exam.json");
                if (Files.exists(metadataFile)) {
                    exams.add(storageService.readJson(metadataFile, ExamMetadata.class));
                }
            }
        }

        exams.sort(Comparator.comparing(ExamMetadata::getCreatedAt).reversed());
        return exams;
    }

    @Override
    public ExamMetadata getExam(String examId) throws IOException {
        Path metadataFile = Path.of(examPath, examId, "exam.json");
        if (!Files.exists(metadataFile)) {
            throw new NoSuchFileException("Exam not found: " + examId);
        }
        return storageService.readJson(metadataFile, ExamMetadata.class);
    }

    @Override
    public List<Question> loadQuestions(String examId) throws IOException {
        Path csvFile = Path.of(examPath, examId, "questions.csv");
        if (!Files.exists(csvFile)) {
            throw new NoSuchFileException("Questions not found for exam: " + examId);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreHeaderCase(true)
                .build();

        List<Question> questions = new ArrayList<>();
        try (Reader reader = new InputStreamReader(Files.newInputStream(csvFile), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            int rowNumber = 1;
            for (CSVRecord record : parser) {
                questions.add(new Question(
                        rowNumber++,
                        record.get("Question"),
                        record.get("Option1"),
                        record.get("Option2"),
                        record.get("Option3"),
                        record.get("Option4"),
                        record.get("CorrectAnswer")));
            }
        }
        return questions;
    }

    @Override
    public List<Question> loadQuestionsPage(String examId, int page, int pageSize) throws IOException {
        List<Question> all = loadQuestions(examId);
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= all.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        return all.subList(fromIndex, toIndex);
    }

    @Override
    public void deleteExam(String examId) throws IOException {
        Path examDir = Path.of(examPath, examId);
        if (!Files.exists(examDir)) {
            throw new NoSuchFileException("Exam not found: " + examId);
        }
        storageService.deleteDirectory(examDir);
        log.info("Exam deleted: {}", examId);
    }

    @Override
    public void updateTimerSettings(String examId, boolean timerEnabled, int duration) throws IOException {
        ExamMetadata existing = getExam(examId);
        ExamMetadata updated = ExamMetadata.builder()
                .examId(existing.getExamId())
                .title(existing.getTitle())
                .subject(existing.getSubject())
                .studentClass(existing.getStudentClass())
                .questionCount(existing.getQuestionCount())
                .timerEnabled(timerEnabled)
                .duration(duration)
                .createdAt(existing.getCreatedAt())
                .build();
        storageService.writeJson(Path.of(examPath, examId, "exam.json"), updated);
        log.info("Timer updated for exam {}: enabled={}, duration={}", examId, timerEnabled, duration);
    }

    private boolean titleExists(String title) throws IOException {
        return listExams().stream()
                .anyMatch(m -> m.getTitle().equalsIgnoreCase(title.trim()));
    }

    private String generateExamId() throws IOException {
        Path examsDir = Path.of(examPath);
        if (!Files.exists(examsDir)) {
            return "exam-001";
        }
        long count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(examsDir, Files::isDirectory)) {
            for (Path ignored : stream) {
                count++;
            }
        }
        return String.format("exam-%03d", count + 1);
    }
}
