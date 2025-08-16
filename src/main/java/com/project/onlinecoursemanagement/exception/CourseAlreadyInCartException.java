package com.project.onlinecoursemanagement.exception;

public class CourseAlreadyInCartException extends RuntimeException {
    public CourseAlreadyInCartException(String message) {
        super(message);
    }
}
