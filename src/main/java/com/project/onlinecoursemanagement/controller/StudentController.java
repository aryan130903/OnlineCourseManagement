package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.payment.service.PaymentService;
import com.project.onlinecoursemanagement.payment.service.PaymentVerificationService;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.project.onlinecoursemanagement.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/courses")
public class StudentController {

    private final CourseService courseService;
    private final PaymentService paymentService;
    private final PaymentVerificationService verificationService;
    private final CartService cartService;
    private final CategoryService categoryService;
    private final StudentQuizService studentQuizService;
    private final CertificateService certificateService;
    private final UserService userService;
//    private final UserRepository userRepository;


    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
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

//    @GetMapping("instructor/{instructor}")
//    public ResponseEntity<?> getCoursesByInstructorEmail(@PathVariable String instructor){
//        Optional<User> user=userRepository.findByUsername(instructor);
//        return courseService.getCoursesByInstructorEmail(instructor);
//    }

    @GetMapping("/instructor-name/{username}")
    public ResponseEntity<?> getCoursesByInstructorUsername(@PathVariable String username) {
        List<CourseDetailDto> courseDtos = courseService.getCoursesByInstructorUsername(username);
        return ResponseEntity.ok(courseDtos);
    }


    @GetMapping("/search")
    public ResponseEntity<List<CourseSummaryDto>> searchCourses(@RequestParam String title) {
        List<CourseSummaryDto> results = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(results);
    }


    @GetMapping("all-category")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }



    @PostMapping("/cart/add/{courseId}")
    public ResponseEntity<String> addToCart(@PathVariable Long courseId, Authentication authentication) {
        cartService.addToCart(courseId, authentication.getName());
        return ResponseEntity.ok("Course added to cart");
    }

    @DeleteMapping("/cart/remove/{courseId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long courseId, Authentication authentication) {
        cartService.removeFromCart(courseId, authentication.getName());
        return ResponseEntity.ok("Course removed from cart");
    }

    @GetMapping("/cart/view")
    public ResponseEntity<?> viewCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.viewCart(authentication.getName()));
    }


    @GetMapping("/enrolled-courses")
    public ResponseEntity<List<CourseDetailDto>> getEnrolledCourses(Authentication authentication) {
        String studentEmail = authentication.getName();
        return ResponseEntity.ok(courseService.getEnrolledCourses(studentEmail));
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<CartSummaryDto> checkout(Authentication authentication) throws Exception {
        CartSummaryDto summary = paymentService.handleCheckout(authentication.getName());
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/cart/verify-payment")
    public ResponseEntity<String> verifyPayment(@RequestParam String paymentLinkId) {
        try {
            String result = verificationService.verifyAndEnroll(paymentLinkId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body( e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(" Internal error: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/quiz")
    public ResponseEntity<QuizResponseDto> getQuiz(
            @PathVariable Long courseId,
           Authentication authentication) {
        String studentEmail = authentication.getName();
        QuizResponseDto quiz = studentQuizService.getQuizForStudent(courseId, studentEmail);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Submit quiz answers for evaluation.
     */
    @PostMapping("/{courseId}/quiz/submit")
    public ResponseEntity<QuizResultDto> submitQuiz(
            @PathVariable Long courseId,
             Authentication authentication,
            @Valid @RequestBody QuizSubmissionDto submission) {

        String studentEmail=authentication.getName();
        QuizResultDto result = studentQuizService.submitQuiz(courseId, studentEmail, submission);
        return ResponseEntity.ok(result);
    }

//    @PostMapping("/dummy-certificate")
//    public ResponseEntity<String> testCertificate() throws Exception {
//        String url = certificateService.generateCertificate(
//                "Aryan Yadav",          // studentName
//                "aryan31424@gmail.com", // studentEmail
//                "Java Spring Boot for Beginners", // courseName
//                95.0                    // score
//        );
//        return ResponseEntity.ok(url);
//    }

    /**
     * Mark a video lecture as watched.
     */
    @PostMapping("/{courseId}/lectures/{lectureId}/watch")
    public ResponseEntity<String> markLectureWatched(
            @PathVariable Long courseId,
            @PathVariable Long lectureId,Authentication authentication) {
        String studentEmail=authentication.getName();
        return ResponseEntity.ok(studentQuizService.markLectureWatched(courseId, lectureId, studentEmail));
    }

    /**
     * Check whether all lectures are completed for a course.
     */
//    @GetMapping("/{courseId}/lectures/completion-status")
//    public ResponseEntity<Boolean> checkAllLecturesWatched(
//            @PathVariable Long courseId,Authentication authentication) {
//        String studentEmail=authentication.getName();
//        return ResponseEntity.ok(studentQuizService.checkAllLecturesWatched(courseId, studentEmail));
//    }

    @GetMapping("/certificates")
    public ResponseEntity<?> getAllCertificates(Authentication authentication) {
        String studentEmail = authentication.getName();
        List<CertificateDto> certificates = certificateService.getCertificatesForStudent(studentEmail);

        if (certificates.isEmpty()) {
            return ResponseEntity.ok("You currently have no certificates");
        }

        return ResponseEntity.ok(certificates);
    }


}
