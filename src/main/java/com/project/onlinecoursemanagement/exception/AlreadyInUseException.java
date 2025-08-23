package com.project.onlinecoursemanagement.exception;

public class AlreadyInUseException extends RuntimeException {
    public AlreadyInUseException(String message) {
        super(message);
    }
}
