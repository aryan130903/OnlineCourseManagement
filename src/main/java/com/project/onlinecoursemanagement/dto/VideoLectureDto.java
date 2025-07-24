package com.project.onlinecoursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoLectureDto{
    private Long id;
    private String title;
    private String videoUrl;
}
