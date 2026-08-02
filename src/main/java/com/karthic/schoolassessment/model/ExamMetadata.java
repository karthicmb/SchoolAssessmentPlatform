package com.karthic.schoolassessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamMetadata {

    private String examId;
    private String title;
    private String subject;

    @JsonProperty("class")
    private String studentClass;

    @JsonProperty("questions")
    private int questionCount;

    private boolean timerEnabled;
    private int duration;
    private String createdAt;
}
