package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public void processPaymentWebhook(String payload) {
        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        if ("payment_link.paid".equals(event)) {
            JSONObject entity = json
                    .getJSONObject("payload")
                    .getJSONObject("payment_link")
                    .getJSONObject("entity");

            String email = entity
                    .getJSONObject("notes")
                    .getString("email");

            User student = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Cart cart = cartRepository.findByStudent(student)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            for (Course course : cart.getCourses()) {
                boolean alreadyEnrolled = enrollmentRepository
                        .findByStudentAndCourse(student, course)
                        .isPresent();

                if (!alreadyEnrolled) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setCourse(course);
                    enrollmentRepository.save(enrollment);
                }
            }

            cart.getCourses().clear();
            cartRepository.save(cart);
        } else {
            throw new IllegalArgumentException("Unsupported event: " + event);
        }
    }
}
