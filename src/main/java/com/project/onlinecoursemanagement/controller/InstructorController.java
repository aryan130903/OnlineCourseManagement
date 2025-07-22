package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseService courseService;

    @GetMapping("/my-courses")
    public ResponseEntity<?> getCourses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return courseService.getCoursesByInstructorUsername(username);
    }


    @PostMapping("add")
    public ResponseEntity<String> addCourse(@RequestBody Course course){
        return courseService.addCourse(course);
    }

    @PatchMapping("/update/{courseId}")
    public ResponseEntity<String> updateCourse(@PathVariable Integer courseId,@RequestBody Course course) {
        return courseService.updateCourse(courseId, course);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable int id){
        return courseService.deleteCourse(id);
    }

}
