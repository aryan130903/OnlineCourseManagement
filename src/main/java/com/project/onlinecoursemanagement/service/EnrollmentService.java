package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
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

    public void placeOrder(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        Cart cart = cartRepository.findByStudent(student).orElseThrow();


        if (cart.getCourses() == null || cart.getCourses().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Add courses before placing order.");
        }

        for (Course course : cart.getCourses()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrolledAt(LocalDateTime.now());

            enrollmentRepository.save(enrollment);
        }

        cart.getCourses().clear();
        cartRepository.save(cart);
    }

    public List<Course> getEnrolledCourses(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        return enrollments.stream()
                .map(Enrollment::getCourse)
                .toList();
    }
}

