package com.karthic.schoolassessment.service;

import com.karthic.schoolassessment.exception.CsvValidationException;
import com.karthic.schoolassessment.model.Question;

import java.io.IOException;
import java.util.List;

/**
 * Parses and validates CSV question papers uploaded by Admin.
 */
public interface CsvService {

    List<Question> parseAndValidate(byte[] csvContent) throws IOException, CsvValidationException;
}
