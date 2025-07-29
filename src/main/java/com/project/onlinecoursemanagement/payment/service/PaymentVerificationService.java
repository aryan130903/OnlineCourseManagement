package com.project.onlinecoursemanagement.payment.service;

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

            // Parse full JSON of paymentLink

            JSONObject json = new JSONObject(paymentLink.toString());
//            System.out.println(json);

            if (!"paid".equals(json.getString("status"))) {
                return "Payment not completed. Status: " + json.getString("status");
            }

            System.out.println(json.getString("status"));

            JSONObject notes = json.getJSONObject("notes");
            String email = notes.getString("email");

            System.out.println(email);


            User student = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));


            System.out.println("user fetched");

            for (Course course : cart.getCourses()) {
                boolean alreadyEnrolled = enrollmentRepository
                        .findByStudentAndCourse(student, course)
                        .isPresent();

//

                if (!alreadyEnrolled) {

//                    System.out.println(course.getId());
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
        } catch (Exception e) {
            e.printStackTrace();
            return "Error verifying payment: " + e.getMessage();
        }
    }
}
