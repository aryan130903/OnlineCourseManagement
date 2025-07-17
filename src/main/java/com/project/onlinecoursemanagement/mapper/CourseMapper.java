package com.project.onlinecoursemanagement.mapper;

import com.project.onlinecoursemanagement.dto.CourseDto;
import com.project.onlinecoursemanagement.dto.InstructorDto;
import com.project.onlinecoursemanagement.model.Course;

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

        return dto;
    }
}
