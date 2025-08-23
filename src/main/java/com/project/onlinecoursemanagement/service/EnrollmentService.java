package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.UserDto;

import java.util.List;

public interface EnrollmentService {
    void placeOrder(String username);

    List<UserDto> getStudentsByCourse(Long courseId, String instructorEmail);
}
