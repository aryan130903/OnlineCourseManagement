package com.project.onlinecoursemanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizResponseDto {
    private Long id;
    private String title;
    private List<QuestionResponseDto> questions;
}

