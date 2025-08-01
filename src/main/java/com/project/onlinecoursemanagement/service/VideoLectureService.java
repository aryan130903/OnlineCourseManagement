package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.model.VideoLecture;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.VideoLectureRepository;
import com.project.onlinecoursemanagement.service.cloud.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.onlinecoursemanagement.exception.VideoUploadException;

@Service
public class VideoLectureService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoLectureRepository videoLectureRepository;

    @Autowired
    private VideoUploadService videoUploadService;

    public VideoLecture uploadVideoToCourse(Long courseId, String title, MultipartFile file, String instructorEmail) throws Exception {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User instructor = course.getInstructor();
        if (instructor == null || !instructor.getEmail().equals(instructorEmail)) {
            throw new AccessDeniedException("You are not allowed to modify this course.");
        }

        String videoUrl;
        try {
            videoUrl = videoUploadService.uploadVideo(file);
        } catch (Exception e) {
            throw new VideoUploadException("Failed to upload video: " + e.getMessage());
        }

        VideoLecture video = new VideoLecture();
        video.setTitle(title);
        video.setVideoUrl(videoUrl);
        video.setCourse(course);

        return videoLectureRepository.save(video);
    }
}
