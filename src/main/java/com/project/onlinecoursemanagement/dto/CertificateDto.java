package com.project.onlinecoursemanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateDto {
    private String courseName;
    private String fileUrl;  // Cloudinary URL
    private LocalDate issueDate;
}

