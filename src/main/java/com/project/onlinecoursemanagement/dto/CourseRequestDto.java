package com.project.onlinecoursemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CourseRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Category name is required")
    private String categoryName;

//    @NotBlank(message = "Instructor email is required")
//    @Email(message = "Invalid instructor email format")
//    private String instructorEmail;

//    private List<@Valid VideoLectureDto> videoLectures;
}
