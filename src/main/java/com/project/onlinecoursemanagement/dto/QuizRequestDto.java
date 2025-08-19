package com.project.onlinecoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizRequestDto {

    @NotBlank(message = "Quiz title is required")
    private String title;
}
