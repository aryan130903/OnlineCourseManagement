package com.project.onlinecoursemanagement.service.impl;

import com.project.onlinecoursemanagement.exception.CourseNotFoundException;
import com.project.onlinecoursemanagement.exception.UnauthorizedAccessException;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.model.VideoLecture;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.VideoLectureRepository;
import com.project.onlinecoursemanagement.service.VideoLectureService;
import com.project.onlinecoursemanagement.service.cloud.CloudVideoUploadService;
//import com.project.onlinecoursemanagement.service.cloud.VideoUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.onlinecoursemanagement.exception.VideoUploadException;

@Service
@RequiredArgsConstructor
public class VideoLectureServiceImpl implements VideoLectureService {

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final VideoLectureRepository videoLectureRepository;

    @Autowired
    private final CloudVideoUploadService cloudVideoUploadService;

    public VideoLecture uploadVideoToCourse(Long courseId, String title, MultipartFile file, String instructorEmail)  {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        User instructor = course.getInstructor();
        if (instructor == null || !instructor.getEmail().equals(instructorEmail)) {
            throw new UnauthorizedAccessException("You are not allowed to modify this course.");
        }

        if (videoLectureRepository.existsByCourseIdAndTitle(courseId, title)) {
            throw new VideoUploadException("A video with this title already exists in the course.");
        }


        String videoUrl;
        try {
            videoUrl = cloudVideoUploadService.uploadVideo(file);
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
