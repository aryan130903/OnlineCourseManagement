package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.dto.CategoryDto;
import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.service.CategoryService;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    private final CourseService courseService;

    private final CategoryService categoryService;

    @GetMapping("/all-user")
    public ResponseEntity<List<User>> getAllUser(){
        return userService.getAllUser();
    }

    @GetMapping("/all-courses")
    public ResponseEntity<?> getAllCourse(){
        return courseService.getAllCourses();
    }


    @GetMapping("/category/all-category")
    public ResponseEntity<?> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @PostMapping("/category/add")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
//        categoryService.addCategory(categoryDto);
//        return ResponseEntity.status(HttpStatus.CREATED);
        return categoryService.addCategory(categoryDto);

    }
    
    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id);
    }
}
