package com.project.onlinecoursemanagement.validation;

import com.project.onlinecoursemanagement.dto.QuestionRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCorrectAnswerImpl implements ConstraintValidator<ValidCorrectAnswer, QuestionRequestDto> {

    @Override
    public boolean isValid(QuestionRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        boolean isValid = dto.getCorrectAnswer() != null &&
                (dto.getCorrectAnswer().equalsIgnoreCase(dto.getOption1())
                        || dto.getCorrectAnswer().equalsIgnoreCase(dto.getOption2())
                        || dto.getCorrectAnswer().equalsIgnoreCase(dto.getOption3())
                        || dto.getCorrectAnswer().equalsIgnoreCase(dto.getOption4()));

        if (!isValid) {
            // Disable default violation message
            context.disableDefaultConstraintViolation();
            // Add custom message for the correctAnswer field
            context.buildConstraintViolationWithTemplate(
                    "Correct answer must match one of the provided options"
            ).addPropertyNode("correctAnswer").addConstraintViolation();
        }

        return isValid;
    }
}
