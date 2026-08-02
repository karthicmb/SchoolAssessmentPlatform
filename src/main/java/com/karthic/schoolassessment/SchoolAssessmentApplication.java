package com.karthic.schoolassessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolAssessmentApplication {

    private static final Logger log = LoggerFactory.getLogger(SchoolAssessmentApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SchoolAssessmentApplication.class, args);
        log.info("School Assessment Platform started successfully");
    }
}
