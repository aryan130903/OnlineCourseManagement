package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CourseSummaryDto;
import com.project.onlinecoursemanagement.exception.*;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.CartRepository;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public void addToCart(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        Cart cart = cartRepository.findByStudent(student)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setStudent(student);
                    return cartRepository.save(newCart);
                });

        if (cart.getCourses().contains(course)) {
            throw new CourseAlreadyInCartException("Course already exists in cart for user: " + email);
        }

        cart.getCourses().add(course);
        cartRepository.save(cart);
    }


    public void removeFromCart(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Cart cart = cartRepository.findByStudent(student)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + email));

        if (cart.getCourses().isEmpty()) {
            throw new EmptyCartException("Cart is already empty for user: " + email);
        }

        boolean removed = cart.getCourses().removeIf(course -> course.getId().equals(courseId));

        if (!removed) {
            throw new CourseNotFoundException("Course not found in cart with ID: " + courseId);
        }

        cartRepository.save(cart);
    }


    public Set<CourseSummaryDto> viewCart(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return cartRepository.findByStudent(student)
                .map(cart -> cart.getCourses()
                        .stream()
                        .map(CourseMapper::toSummaryDto)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + email));
    }


}
