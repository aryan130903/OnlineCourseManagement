package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.VideoLecture;
import org.springframework.web.multipart.MultipartFile;

public interface VideoLectureService {

    VideoLecture uploadVideoToCourse(Long courseId, String title, MultipartFile file, String instructorEmail);
}
