package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/student/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(Principal principal) {
        enrollmentService.placeOrder(principal.getName());
        return ResponseEntity.ok("Order placed and courses enrolled");
    }

    @GetMapping
    public ResponseEntity<List<Course>> viewEnrolledCourses(Principal principal) {
        return ResponseEntity.ok(enrollmentService.getEnrolledCourses(principal.getName()));
    }
}

