package com.project.onlinecoursemanagement.payment.service;

import com.project.onlinecoursemanagement.dto.EmailRequestDto;
import com.project.onlinecoursemanagement.exception.CartNotFoundException;
import com.project.onlinecoursemanagement.exception.EmptyCartException;
import com.project.onlinecoursemanagement.exception.PaymentVerificationException;
import com.project.onlinecoursemanagement.exception.UserNotFoundException;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.publisher.RabbitMQProducer;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Transactional
    public String verifyAndEnroll(String paymentLinkId) {
        try {
            log.info("Starting payment verification for paymentLinkId: {}", paymentLinkId);

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
            PaymentLink paymentLink = razorpayClient.paymentLink.fetch(paymentLinkId);
            JSONObject json = new JSONObject(paymentLink.toString());

            if (!"paid".equals(json.getString("status"))) {
                log.warn("Payment not completed for paymentLinkId: {}. Status: {}", paymentLinkId, json.getString("status"));
                return "Payment not completed. Status: " + json.getString("status");
            }

            String email = json.getJSONObject("notes").getString("email");
            log.info("Payment successful. Fetching user by email: {}", email);

            User student = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Student not found with email"));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for student"));

            if (cart.getCourses() == null || cart.getCourses().isEmpty()) {
                log.warn("Empty cart for student: {}", email);
                throw new EmptyCartException("Cart is empty for student");
            }

            for (Course course : cart.getCourses()) {
                boolean alreadyEnrolled = enrollmentRepository.findByStudentAndCourse(student, course).isPresent();
                if (!alreadyEnrolled) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setCourse(course);
                    enrollment.setEnrolledAt(LocalDateTime.now());
                    enrollmentRepository.save(enrollment);
                    log.info("Enrolled student {} to course {}", email, course.getTitle());
                }
            }

            List<String> courseNames = cart.getCourses().stream().map(Course::getTitle).toList();

            cart.getCourses().clear();
            cartRepository.save(cart);

            String courseListText = courseNames.stream()
                    .collect(Collectors.joining("\n - ", "\n - ", ""));

            EmailRequestDto emailDto = new EmailRequestDto(
                    email,
                    "Course Purchase Successful",
                    "Hi " + student.getUsername() + ",\n\n"
                            + "Thank you for purchasing the course(s)! You are now enrolled in:\n"
                            + courseListText
            );

            rabbitMQProducer.sendEmailNotification(emailDto);
            log.info("Sent enrollment email to {}", email);

            return "Payment verified, user enrolled, cart cleared.";

        } catch (UserNotFoundException | CartNotFoundException | EmptyCartException | IllegalStateException ex) {
            log.error("Known error during payment verification: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Unexpected error during payment verification", e);
            throw new PaymentVerificationException("Failed to verify Razorpay payment: " + e.getMessage());
        }
    }

}
