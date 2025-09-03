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
            // Remove the default violation
            context.disableDefaultConstraintViolation();

            // Use the annotation's message instead of hardcoding
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    ).addPropertyNode("correctAnswer")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
