package com.project.onlinecoursemanagement.dto;

import com.project.onlinecoursemanagement.validation.ValidCorrectAnswer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ValidCorrectAnswer
public class QuestionRequestDto {
    @NotBlank(message = "Question text is required")
    private String text;

    @NotBlank(message = "Option 1 is required")
    private String option1;

    @NotBlank(message = "Option 2 is required")
    private String option2;

    @NotBlank(message = "Option 3 is required")
    private String option3;

    @NotBlank(message = "Option 4 is required")
    private String option4;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer; 
}

