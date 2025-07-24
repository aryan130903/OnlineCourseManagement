package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CourseSummaryDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int videoLectureCount;
    private String instructorName;
    private String category;
}
