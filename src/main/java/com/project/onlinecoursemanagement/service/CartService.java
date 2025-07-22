package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.respository.CartRepository;
import com.project.onlinecoursemanagement.respository.CourseRepository;
import com.project.onlinecoursemanagement.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public void addToCart(Integer courseId, String email) {
        User student = userRepository.findByEmail(email).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Cart cart = cartRepository.findByStudent(student)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setStudent(student);
                    return cartRepository.save(newCart);
                });

        cart.getCourses().add(course);
        cartRepository.save(cart);
    }

    public void removeFromCart(Long courseId, String email) {
        User student = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByStudent(student).orElseThrow();

        cart.getCourses().removeIf(course -> course.getId().equals(courseId));
        cartRepository.save(cart);
    }

    public Set<Course> viewCart(String email) {
        User student = userRepository.findByEmail(email).orElseThrow();
        return cartRepository.findByStudent(student)
                .map(Cart::getCourses)
                .orElse(new HashSet<>());
    }
}
