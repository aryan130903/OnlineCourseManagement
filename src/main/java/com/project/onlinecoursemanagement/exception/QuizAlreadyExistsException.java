package com.project.onlinecoursemanagement.exception;


public class QuizAlreadyExistsException extends RuntimeException {

    public QuizAlreadyExistsException(String message) {
        super(message);
    }
}