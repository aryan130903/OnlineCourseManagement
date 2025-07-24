package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/courses")
public class StudentController {

    private final CourseService courseService;

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<?> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, Authentication authentication) {
        String studentEmail = authentication.getName();
        return ResponseEntity.ok(courseService.getCourseById(id, studentEmail));
    }

    @GetMapping("category/{category}")
    public ResponseEntity<?> getCourseByCategory(@PathVariable String category){
        return courseService.getCourseByCategory(category);
    }

    @GetMapping("instructor/{id}")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable Integer id){
        return courseService.getCoursesByInstructor(id);
    }

    @GetMapping("categories")
    public ResponseEntity<?> getAllCategory(){
        return courseService.getAllCategory();
    }

    @GetMapping("/enrolled-courses")
    public ResponseEntity<?> getEnrolledCourses(Authentication authentication) {
        String studentEmail = authentication.getName();
        return ResponseEntity.ok(courseService.getEnrolledCourses(studentEmail));
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<CartSummaryDto> checkout(Principal principal) throws Exception {
        CartSummaryDto summary = paymentService.handleCheckout(principal.getName());
        return ResponseEntity.ok(summary);
    }




}
