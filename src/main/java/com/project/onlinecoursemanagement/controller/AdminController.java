package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.dto.CategoryDto;
import com.project.onlinecoursemanagement.dto.CourseDto;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.service.CategoryService;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }


    @GetMapping("/category/all-category")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
    List<CategoryDto> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/category/add")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
//        categoryService.addCategory(categoryDto);
//        return ResponseEntity.status(HttpStatus.CREATED);
        return ResponseEntity.ok(categoryService.addCategory(categoryDto));

    }
    
    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
