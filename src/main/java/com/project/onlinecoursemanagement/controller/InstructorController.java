package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.dto.CourseDetailDto;
import com.project.onlinecoursemanagement.dto.CourseRequestDto;
import com.project.onlinecoursemanagement.dto.VideoLectureUpdateDto;
import com.project.onlinecoursemanagement.exception.VideoUploadException;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.VideoLectureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseService courseService;

    @Autowired
    private VideoLectureService videoLectureService;

    @GetMapping("/my-courses")
    public ResponseEntity<?> getCourses(Authentication authentication) {
        String email = authentication.getName();
        return courseService.getCoursesByInstructorUsername(email);
    }

    @GetMapping("/my-course/{courseId}")
    public ResponseEntity<CourseDetailDto> getInstructorCourseById(
            @PathVariable Long courseId,
            Authentication authentication) {
        String instructorEmail = authentication.getName();
        CourseDetailDto course = courseService.getInstructorCourseById(courseId, instructorEmail);
        return ResponseEntity.ok(course);
    }



    @PostMapping("/add")
    public ResponseEntity<String> addCourse(@Valid @RequestBody CourseRequestDto dto,Authentication authentication) {
        String email = authentication.getName();
        return courseService.addCourse(dto,email);
    }


    @PutMapping("/update/{courseId}")
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequestDto courseRequestDto, Authentication authentication) {
        String email = authentication.getName();
        return courseService.updateCourse(courseId, courseRequestDto,email);
    }

    @DeleteMapping("/my-courses/{courseId}/video-lectures/{videoId}")
    public ResponseEntity<String> deleteVideoLecture(
            @PathVariable Long courseId,
            @PathVariable Long videoId,
            Authentication authentication) {

        String instructorEmail = authentication.getName();
        return courseService.deleteVideoLecture(courseId, videoId, instructorEmail);
    }



    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id,Authentication authentication){
        String email=authentication.getName();
        return courseService.deleteCourse(id,email);
    }



    @PostMapping("/upload-video/{courseId}")
    public ResponseEntity<String> uploadVideoToCourse(
            @PathVariable Long courseId,
            @RequestParam("title") @NotBlank(message = "Title cannot be empty") String title,
            @RequestParam("file") @NotNull(message = "File cannot be null") MultipartFile file,
            Authentication authentication) {

        if (file.isEmpty()) {
            throw new VideoUploadException("File cannot be empty");
        }

        String instructorEmail = authentication.getName();
        videoLectureService.uploadVideoToCourse(courseId, title, file, instructorEmail);

        return ResponseEntity.ok("Video uploaded successfully!");
    }

}
