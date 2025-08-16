package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.dto.CartSummaryDto;
import com.project.onlinecoursemanagement.dto.CourseDetailDto;
import com.project.onlinecoursemanagement.dto.CourseSummaryDto;
import com.project.onlinecoursemanagement.service.CartService;
import com.project.onlinecoursemanagement.service.CategoryService;
import com.project.onlinecoursemanagement.service.CourseService;
import com.project.onlinecoursemanagement.payment.service.PaymentService;
import com.project.onlinecoursemanagement.payment.service.PaymentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/courses")
public class StudentController {

    private final CourseService courseService;

    private final PaymentService paymentService;

    private final PaymentVerificationService verificationService;

    private final CartService cartService;

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, Authentication authentication) {
        String studentEmail = authentication.getName();
        return courseService.getCourseById(id, studentEmail);
    }

    @GetMapping("category/{category}")
    public ResponseEntity<?> getCourseByCategory(@PathVariable String category){
        return courseService.getCourseByCategory(category);
    }

    @GetMapping("instructor/{id}")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable Integer id){
        return courseService.getCoursesByInstructor(id);
    }


    @GetMapping("/search")
    public ResponseEntity<List<CourseSummaryDto>> searchCourses(@RequestParam String title) {
        List<CourseSummaryDto> results = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(results);
    }


    @GetMapping("all-category")
    public ResponseEntity<?> getAllCategory(){
        return categoryService.getAllCategory();
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


}
