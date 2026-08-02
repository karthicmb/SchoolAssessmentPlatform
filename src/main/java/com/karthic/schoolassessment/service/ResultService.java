package com.karthic.schoolassessment.service;

import com.karthic.schoolassessment.model.ExamMetadata;
import com.karthic.schoolassessment.model.ExamResult;
import com.karthic.schoolassessment.model.Question;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ResultService {

    ExamResult evaluate(ExamMetadata metadata, List<Question> questions, Map<Integer, String> studentAnswers);

    void saveResult(ExamResult result) throws IOException;

    List<ExamResult> listResults() throws IOException;

    ExamResult getResult(String resultId) throws IOException;

    void deleteResult(String resultId) throws IOException;
}
