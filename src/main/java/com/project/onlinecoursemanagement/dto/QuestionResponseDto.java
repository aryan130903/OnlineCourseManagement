package com.project.onlinecoursemanagement.dto;

import lombok.Data;

@Data
public class QuestionResponseDto {
    private Long id;
    private String text;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}
