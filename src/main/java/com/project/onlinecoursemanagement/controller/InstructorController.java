package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.exception.VideoUploadException;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.service.EnrollmentService;
import com.project.onlinecoursemanagement.service.QuizService;
import com.project.onlinecoursemanagement.service.VideoLectureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseService courseService;

    private final VideoLectureService videoLectureService;

    private final QuizService quizService;

    private final EnrollmentService enrollmentService;

    @GetMapping("/my-courses")
    public ResponseEntity<?> getCourses(Authentication authentication) {
        String email = authentication.getName();
        return courseService.getCoursesByInstructorEmail(email);
    }

    @GetMapping("/enrolled-student/{courseId}")
    public ResponseEntity<List<UserDto>> getStudents(
            @PathVariable Long courseId,
            Authentication authentication) {

        // Get the logged-in instructor email from Authentication
        String instructorEmail = authentication.getName();

        // Delegate all business logic to service
        List<UserDto> students = enrollmentService.getStudentsByCourse(courseId, instructorEmail);

        return ResponseEntity.ok(students);
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
        return ResponseEntity.ok(courseService.addCourse(dto,email));
    }


    @PutMapping("/update/{courseId}")
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequestDto courseRequestDto, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseRequestDto,email));
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
        return ResponseEntity.ok(courseService.deleteCourse(id,email));
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


//    -------------------QUIZ MAPPINGS----------------

    @PostMapping("/quiz/create/{courseId}")
    public ResponseEntity<?> createQuiz(
            @PathVariable Long courseId,
            @Valid @RequestBody QuizRequestDto dto,Authentication authentication) {
        String email = authentication.getName();
        quizService.createQuiz(courseId, dto,email);
        return ResponseEntity.ok("Quiz created successfully");
    }

    @PostMapping("/quiz/{quizId}/add-question")
    public ResponseEntity<?> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequestDto dto,Authentication authentication) {
        String email = authentication.getName();
        quizService.addQuestion(quizId, dto,email);
        return ResponseEntity.ok("Question added successfully");
    }

    @GetMapping("/quiz/course/{courseId}")
    public ResponseEntity<QuizResponseDto> getQuiz(@PathVariable Long courseId,Authentication authentication) {
        String email=authentication.getName();
        QuizResponseDto quizDto = quizService.getQuizByCourse(courseId,email);
        return ResponseEntity.ok(quizDto);
    }

//    @DeleteMapping("/quiz/{quizId}")
//    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
//        quizService.deleteQuiz(quizId);
//        return ResponseEntity.ok("Quiz deleted successfully");
//    }

    @PutMapping("/quiz/{quizId}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizRequestDto dto) {
        QuizRequestDto updatedQuiz = quizService.updateQuiz(quizId, dto);
        return ResponseEntity.ok(updatedQuiz);
    }

    // Delete a question by ID
    @DeleteMapping("/quiz/{quizId}/question/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long quizId ,@PathVariable Long questionId,Authentication authentication) {
        String email=authentication.getName();
        quizService.deleteQuestion(quizId,questionId,email);
        return ResponseEntity.ok("Question deleted successfully");
    }


}
