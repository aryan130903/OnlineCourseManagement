package com.project.onlinecoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerDto {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Selected answer cannot be blank")
    private String selectedAnswer;
}
