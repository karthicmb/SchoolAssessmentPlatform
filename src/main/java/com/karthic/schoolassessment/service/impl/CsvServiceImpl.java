package com.karthic.schoolassessment.service.impl;

import com.karthic.schoolassessment.exception.CsvValidationException;
import com.karthic.schoolassessment.model.Question;
import com.karthic.schoolassessment.service.CsvService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceImpl implements CsvService {

    private static final Logger log = LoggerFactory.getLogger(CsvServiceImpl.class);

    private static final List<String> REQUIRED_HEADERS =
            List.of("question", "option1", "option2", "option3", "option4", "correctanswer");

    @Override
    public List<Question> parseAndValidate(byte[] csvContent) throws IOException, CsvValidationException {
        if (csvContent == null || csvContent.length == 0) {
            throw new CsvValidationException("CSV file is empty.");
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreHeaderCase(true)
                .build();

        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(csvContent), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            validateHeaders(parser.getHeaderNames());

            List<Question> questions = new ArrayList<>();
            int rowNumber = 1;

            for (CSVRecord record : parser) {
                questions.add(validateAndBuild(record, rowNumber));
                rowNumber++;
            }

            if (questions.isEmpty()) {
                throw new CsvValidationException("CSV file contains no questions.");
            }

            log.info("CSV validated: {} questions found", questions.size());
            return questions;
        }
    }

    private void validateHeaders(List<String> headers) throws CsvValidationException {
        List<String> lowerHeaders = headers.stream().map(String::toLowerCase).toList();
        for (String required : REQUIRED_HEADERS) {
            if (!lowerHeaders.contains(required)) {
                throw new CsvValidationException(
                        "Missing required column: '" + required + "'. " +
                        "Expected headers: Question, Option1, Option2, Option3, Option4, CorrectAnswer");
            }
        }
    }

    private Question validateAndBuild(CSVRecord record, int rowNumber) throws CsvValidationException {
        String question    = record.get("Question");
        String option1     = record.get("Option1");
        String option2     = record.get("Option2");
        String option3     = record.get("Option3");
        String option4     = record.get("Option4");
        String correctAnswer = record.get("CorrectAnswer");

        if (isBlank(question))     throw new CsvValidationException("Row " + rowNumber + ": Question cannot be empty.");
        if (isBlank(option1))      throw new CsvValidationException("Row " + rowNumber + ": Option1 cannot be empty.");
        if (isBlank(option2))      throw new CsvValidationException("Row " + rowNumber + ": Option2 cannot be empty.");
        if (isBlank(option3))      throw new CsvValidationException("Row " + rowNumber + ": Option3 cannot be empty.");
        if (isBlank(option4))      throw new CsvValidationException("Row " + rowNumber + ": Option4 cannot be empty.");
        if (isBlank(correctAnswer)) throw new CsvValidationException("Row " + rowNumber + ": CorrectAnswer cannot be empty.");

        if (!List.of(option1, option2, option3, option4).contains(correctAnswer)) {
            throw new CsvValidationException(
                    "Row " + rowNumber + ": CorrectAnswer '" + correctAnswer + "' does not match any option.");
        }

        return new Question(rowNumber, question, option1, option2, option3, option4, correctAnswer);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
