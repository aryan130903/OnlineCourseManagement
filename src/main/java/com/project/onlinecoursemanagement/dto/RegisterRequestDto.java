package com.project.onlinecoursemanagement.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false,unique = true)
    private String email;

    private String role;

    @Column(nullable = false)
    private String password;
}
