package com.project.onlinecoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLectureDto {

    private Long id; // Optional for new videos

    @NotBlank(message = "Video title is required")
    private String title;

    @NotBlank(message = "Video URL is required")
    private String videoUrl;
}
