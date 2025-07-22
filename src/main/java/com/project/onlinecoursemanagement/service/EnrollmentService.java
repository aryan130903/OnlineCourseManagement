package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.respository.CartRepository;
import com.project.onlinecoursemanagement.respository.EnrollmentRepository;
import com.project.onlinecoursemanagement.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public void placeOrder(String email) {
        User student = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByStudent(student).orElseThrow();

        for (Course course : cart.getCourses()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrolledAt(LocalDateTime.now());

            enrollmentRepository.save(enrollment);
        }

        // Clear cart after ordering
        cart.getCourses().clear();
        cartRepository.save(cart);
    }

    public List<Course> getEnrolledCourses(String email) {
        User student = userRepository.findByEmail(email).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        return enrollments.stream()
                .map(Enrollment::getCourse)
                .toList();
    }
}
