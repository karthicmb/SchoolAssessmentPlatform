package com.karthic.schoolassessment.dto;

import lombok.Data;

@Data
public class UploadExamRequest {

    private String title;
    private String subject;
    private String studentClass;
    private boolean timerEnabled;
    private int duration = 60;
}
