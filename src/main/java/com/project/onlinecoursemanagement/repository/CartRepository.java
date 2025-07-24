package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Cart;
import com.project.onlinecoursemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByStudent(User student);
}
