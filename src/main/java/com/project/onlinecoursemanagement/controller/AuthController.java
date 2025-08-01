package com.project.onlinecoursemanagement.controller;


import com.project.onlinecoursemanagement.dto.LoginRequestDto;
import com.project.onlinecoursemanagement.dto.RegisterRequestDto;
import com.project.onlinecoursemanagement.model.Role;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.repository.RoleRepository;
import com.project.onlinecoursemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final RoleRepository roleRepository;

    @PostMapping("/public/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(userService.loginUser(loginRequestDto));
    }

    @PostMapping("/public/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        userService.registerUser(registerRequestDto);
        return ResponseEntity.ok("User registered");
    }


}
