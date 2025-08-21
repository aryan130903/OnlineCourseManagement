package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.LoginRequestDto;
import com.project.onlinecoursemanagement.dto.RegisterRequestDto;
import com.project.onlinecoursemanagement.dto.UserDto;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.security.jwt.JwtAuthenticationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    UserDto getUserByEmail(String email);
    // Register a new user
    void registerUser(RegisterRequestDto dto);

    // Authenticate and return JWT token
    JwtAuthenticationResponse loginUser(LoginRequestDto loginRequestDto);

    // Fetch all users
    ResponseEntity<List<User>> getAllUser();
}
