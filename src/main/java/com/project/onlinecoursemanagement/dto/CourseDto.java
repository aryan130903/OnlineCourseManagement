package com.project.onlinecoursemanagement.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseDto {
    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private InstructorDto instructor;
}
