package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.VideoLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseService courseService;

    @Autowired
    private VideoLectureService videoLectureService;

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
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId,@RequestBody Course course) {
        return courseService.updateCourse(courseId, course);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id){
        return courseService.deleteCourse(id);
    }



    @PostMapping("/upload-video/{courseId}")
    public ResponseEntity<?> uploadVideoToCourse(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String instructorEmail = authentication.getName();

            videoLectureService.uploadVideoToCourse(courseId, title, file, instructorEmail);

            return ResponseEntity.ok("Video uploaded successfully!");

        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
