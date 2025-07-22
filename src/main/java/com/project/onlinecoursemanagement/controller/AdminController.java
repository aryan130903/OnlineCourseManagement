package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.service.CategoryService;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.UserService;
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

    @GetMapping("/all-course")
    public ResponseEntity<?> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping("/category/all-category")
    public ResponseEntity<List<Category>> getAllCategory(){
        return categoryService.getAllCategory();
    }
    @PostMapping("/category/add")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id);
    }
}
