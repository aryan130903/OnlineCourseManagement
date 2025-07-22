package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/courses")
public class StudentController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<?> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer id){
        return courseService.getCourseById(id);
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



}
