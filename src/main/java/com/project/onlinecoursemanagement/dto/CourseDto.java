package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

// USED WHEN STUDENT ACCESSES THE COURSES (EITHER ALL OR BY ID OR INSTRUCTOR)

@Data
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private InstructorDto instructor;
    private int videoLectureCount;
}
