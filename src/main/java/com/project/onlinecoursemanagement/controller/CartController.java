package com.project.onlinecoursemanagement.controller;

import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/student/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{courseId}")
    public ResponseEntity<String> addToCart(@PathVariable Integer courseId, Principal principal) {
        cartService.addToCart(courseId, principal.getName());
        return ResponseEntity.ok("Course added to cart");
    }

    @DeleteMapping("/remove/{courseId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long courseId, Principal principal) {
        cartService.removeFromCart(courseId, principal.getName());
        return ResponseEntity.ok("Course removed from cart");
    }

    @GetMapping
    public ResponseEntity<?> viewCart(Principal principal) {
        return ResponseEntity.ok(cartService.viewCart(principal.getName()));
    }
}

