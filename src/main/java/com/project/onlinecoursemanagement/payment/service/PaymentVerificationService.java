package com.project.onlinecoursemanagement.payment.service;

import com.project.onlinecoursemanagement.exception.CartNotFoundException;
import com.project.onlinecoursemanagement.exception.PaymentVerificationException;
import com.project.onlinecoursemanagement.exception.UserNotFoundException;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Transactional
    public String verifyAndEnroll(String paymentLinkId) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
            PaymentLink paymentLink = razorpayClient.paymentLink.fetch(paymentLinkId);
            JSONObject json = new JSONObject(paymentLink.toString());

            if (!"paid".equals(json.getString("status"))) {
                return "Payment not completed. Status: " + json.getString("status");
            }

            String email = json.getJSONObject("notes").getString("email");

            User student = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Student not found with email: " + email));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for student: " + email));

            for (Course course : cart.getCourses()) {
                boolean alreadyEnrolled = enrollmentRepository
                        .findByStudentAndCourse(student, course)
                        .isPresent();

                if (!alreadyEnrolled) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setCourse(course);
                    enrollment.setEnrolledAt(LocalDateTime.now());
                    enrollmentRepository.save(enrollment);
                }
            }

            cart.getCourses().clear();
            cartRepository.save(cart);

            return "Payment verified, user enrolled, cart cleared.";

        } catch (UserNotFoundException | CartNotFoundException | IllegalStateException ex) {
            throw ex;
        } catch (Exception e) {
            throw new PaymentVerificationException("Failed to verify Razorpay payment: " + e.getMessage());
        }
    }
}
