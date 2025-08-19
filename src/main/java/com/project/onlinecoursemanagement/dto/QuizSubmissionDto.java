package com.project.onlinecoursemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionDto {

    @NotEmpty(message = "At least one answer must be provided")
    @Valid   // ensures nested AnswerDto objects are also validated
    private List<AnswerDto> answers;
}

