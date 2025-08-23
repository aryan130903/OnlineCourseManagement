package com.project.onlinecoursemanagement.service.impl;

import com.project.onlinecoursemanagement.dto.UserDto;
import com.project.onlinecoursemanagement.exception.*;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import com.project.onlinecoursemanagement.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public void placeOrder(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Student Not Found"));
        Cart cart = cartRepository.findByStudent(student)
                .orElseThrow(()-> new CartNotFoundException("Cart does not exist"));



        if (cart.getCourses() == null || cart.getCourses().isEmpty()) {
            throw new EmptyCartException("Cart is empty. Add courses before placing order.");
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

    public List<UserDto> getStudentsByCourse(Long courseId, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        // Check if the instructor owns the course
        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new UnauthorizedAccessException("You are not allowed to view students of this course");
        }

        // Fetch enrollments and map to UserDTO
        return enrollmentRepository.findByCourse(course).stream()
                .map(enrollment -> new UserDto(
                        enrollment.getStudent().getEmail(),
                        enrollment.getStudent().getUsername()
                ))
                .toList();
    }
}

