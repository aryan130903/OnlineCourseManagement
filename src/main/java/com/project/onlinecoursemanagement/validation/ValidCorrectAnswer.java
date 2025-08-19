package com.project.onlinecoursemanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCorrectAnswerImpl.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCorrectAnswer {
    String message() default "Correct answer must match one of the provided options";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
