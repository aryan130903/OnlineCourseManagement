package com.project.onlinecoursemanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//USED WHEN ADDING THE COURSE (BY INSTRUCTOR)

@Data
public class CourseRequestDto {
    private String title;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String instructorEmail;
    private List<VideoLectureDto> videoLectures;
}
