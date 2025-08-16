package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// USED WHEN COURSE RETURNED SHOULD CONTAIN VIDEO LECTURE

@Data
@AllArgsConstructor
public class CourseDetailDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String instructorName;
    private String category;
    private List<VideoLectureDto> videoLectures;
}
