package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// USED IN SERVICES LIKE CART WHEN USING VIEW ENDPOINT

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSummaryDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int videoLectureCount;
    private String instructorName;
    private String category;
}
