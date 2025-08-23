package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CourseService {

    List<CourseDto> getAllCourses();

    Object getCourseById(Long courseId, String studentEmail);

    ResponseEntity<?> getCoursesByInstructor(Integer id);

    ResponseEntity<?> getCourseByCategory(String category);

    List<CourseSummaryDto> searchCoursesByTitle(String title);

    String addCourse(CourseRequestDto dto, String email);

    String deleteCourse(Long id, String email);

    String updateCourse(Long courseId, CourseRequestDto dto, String email);

    List<CourseDetailDto> getCoursesByInstructorUsername(String username);

    ResponseEntity<?> getCoursesByInstructorEmail(String email);

    CourseDetailDto getInstructorCourseById(Long courseId, String email);

    ResponseEntity<String> deleteVideoLecture(Long courseId, Long videoId, String instructorEmail);

    List<CourseDetailDto> getEnrolledCourses(String studentEmail);
}
