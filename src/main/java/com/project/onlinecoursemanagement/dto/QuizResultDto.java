package com.project.onlinecoursemanagement.dto;

import lombok.Data;

@Data
public class QuizResultDto {
    private Long quizId;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
    private String certificateUrl;
    private String message;
}
