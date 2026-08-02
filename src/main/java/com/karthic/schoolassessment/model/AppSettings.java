package com.karthic.schoolassessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettings {

    private int defaultExamDuration;
    private int questionsPerPage;
}
