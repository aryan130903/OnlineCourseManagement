package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("courses")
public class CourseController {

    CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService){
        this.courseService=courseService;
    }

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

    @PostMapping("add")
    public ResponseEntity<String> addCourse(@RequestBody Course course){
        return courseService.addCourse(course);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable int id){
        return courseService.deleteCourse(id);
    }

}
