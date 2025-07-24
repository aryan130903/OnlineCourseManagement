package com.project.onlinecoursemanagement.mapper;

import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.model.Course;

import java.util.List;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();

        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());

        if (course.getCategory() != null) {
            dto.setCategory(course.getCategory().getName());
        }

        if (course.getInstructor() != null) {
            InstructorDto instructorDto = new InstructorDto();
            instructorDto.setId(course.getInstructor().getId());
            instructorDto.setUsername(course.getInstructor().getUsername());
            dto.setInstructor(instructorDto);
        }

        dto.setVideoLectureCount(
                course.getVideoLectures() != null ? course.getVideoLectures().size() : 0
        );

        return dto;
    }

    public static CourseSummaryDto toSummaryDto(Course course) {
        return new CourseSummaryDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getVideoLectures() != null ? course.getVideoLectures().size() : 0,
                course.getInstructor() != null ? course.getInstructor().getUsername() : null,
                course.getCategory() != null ? course.getCategory().getName() : null
        );
    }

    public static CourseDetailDto toDetailDto(Course course) {
        List<VideoLectureDto> videoLectureDtos = course.getVideoLectures()
                .stream()
                .map(video -> new VideoLectureDto(
                        video.getId() != null ? video.getId().longValue() : null,
                        video.getTitle(),
                        video.getVideoUrl()
                ))
                .collect(Collectors.toList());

        return new CourseDetailDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getInstructor() != null ? course.getInstructor().getUsername() : null,
                course.getCategory() != null ? course.getCategory().getName() : null,
                videoLectureDtos
        );
    }

}
