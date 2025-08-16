package com.project.onlinecoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VideoLectureUpdateDto {

    @NotBlank(message = "Video title is required")
    private String title;

    @NotBlank(message = "Video URL is required")
    @Pattern(regexp = "https?://.*", message = "Invalid video URL format")
    private String videoUrl;
}
