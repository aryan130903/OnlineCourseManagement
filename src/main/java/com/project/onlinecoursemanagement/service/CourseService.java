package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.exception.CategoryNotFoundException;
import com.project.onlinecoursemanagement.exception.CourseNotFoundException;
import com.project.onlinecoursemanagement.exception.UnauthorizedAccessException;
import com.project.onlinecoursemanagement.exception.UserNotFoundException;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.*;
import com.project.onlinecoursemanagement.repository.CategoryRepository;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.EnrollmentRepository;
import com.project.onlinecoursemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    CourseRepository courseRepository;

    CategoryRepository categoryRepository;

    UserRepository userRepository;

    EnrollmentRepository enrollmentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,UserRepository userRepository,EnrollmentRepository enrollmentRepository){
        this.courseRepository=courseRepository;
        this.categoryRepository=categoryRepository;
        this.userRepository=userRepository;
        this.enrollmentRepository=enrollmentRepository;
    }

    public ResponseEntity<?> getAllCourses() {
        try {
            List<Course> courses = courseRepository.findAll();
            List<CourseDto> courseDtos = courses.stream()
                    .map(CourseMapper::toDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> getCourseById(Long courseId, String studentEmail) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundException("Course not found"));

            User student = userRepository.findByEmail(studentEmail)
                    .orElseThrow(() -> new UserNotFoundException("Student not found"));

            boolean isEnrolled = enrollmentRepository.findByStudentAndCourse(student, course).isPresent();

            if (isEnrolled) {
                return new ResponseEntity<>(CourseMapper.toDetailDto(course), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(CourseMapper.toSummaryDto(course), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching course", HttpStatus.BAD_REQUEST);
        }
    }




    public ResponseEntity<?> getCoursesByInstructor(Integer id) {
        try {
            List<Course> courses = courseRepository.findByInstructorId(id);
            List<CourseDto> courseDtos = courses.stream()
                    .map(CourseMapper::toDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> getCourseByCategory(String category) {
        try {
            List<Course> courses = courseRepository.findByCategory_NameIgnoreCase(category);
            List<CourseDto> courseDtos = courses.stream()
                    .map(CourseMapper::toDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }



    public ResponseEntity<String> addCourse(CourseRequestDto dto) {
        Optional<Course> existingCourse = courseRepository.findByTitleAndInstructorEmail(dto.getTitle(), dto.getInstructorEmail());

        if (existingCourse.isPresent()) {
            return new ResponseEntity<>("Course with the same title already exists for this instructor", HttpStatus.CONFLICT);
        }

        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        User instructor = userRepository.findByEmail(dto.getInstructorEmail())
                .orElseThrow(() -> new UserNotFoundException("Instructor not found"));

        if (!instructor.getRole().getName().equals("ROLE_INSTRUCTOR")) {
            throw new UnauthorizedAccessException("Only instructors can create courses.");
        }


        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        course.setCategory(category);
        course.setInstructor(instructor);

        courseRepository.save(course);
        return new ResponseEntity<>("Course added successfully", HttpStatus.CREATED);
    }



    //    --------DELETE--------
    public ResponseEntity<String> deleteCourse(Long id) {

        if (!courseRepository.existsById(id)) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        try {
            courseRepository.deleteById(id);
            return new ResponseEntity<>("Course deleted", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<String> updateCourse(Long courseId, CourseRequestDto dto) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        Course existingCourse = optionalCourse.get();

        if (!existingCourse.getInstructor().getEmail().equals(dto.getInstructorEmail())) {
            throw new UnauthorizedAccessException("You are not authorized to update this course.");
        }


        if (dto.getTitle() != null) existingCourse.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existingCourse.setDescription(dto.getDescription());
        if (dto.getPrice() != null) existingCourse.setPrice(dto.getPrice());

        if (dto.getCategoryName() != null) {
            Category category = categoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            existingCourse.setCategory(category);
        }

        if (dto.getVideoLectures() != null) {
            Map<Long, VideoLecture> existingVideos = existingCourse.getVideoLectures()
                    .stream()
                    .filter(v -> v.getId() != null)
                    .collect(Collectors.toMap(VideoLecture::getId, v -> v));

            for (VideoLectureDto videoDto : dto.getVideoLectures()) {
                if (videoDto.getId() != null && existingVideos.containsKey(videoDto.getId())) {
                    // Update existing video
                    VideoLecture video = existingVideos.get(videoDto.getId());
                    video.setTitle(videoDto.getTitle());
                    video.setVideoUrl(videoDto.getVideoUrl());
                } else {
                    // Add new video
                    VideoLecture newVideo = new VideoLecture();
                    newVideo.setTitle(videoDto.getTitle());
                    newVideo.setVideoUrl(videoDto.getVideoUrl());
                    newVideo.setCourse(existingCourse);
                    existingCourse.getVideoLectures().add(newVideo);
                }
            }
        }

        courseRepository.save(existingCourse);
        return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
    }




    public ResponseEntity<?> getCoursesByInstructorUsername(String email) {
        try {
            User instructor = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            List<Course> courses = courseRepository.findByInstructor(instructor);

            List<CourseDetailDto> courseDtos = courses.stream()
                    .map(CourseMapper::toDetailDto)
                    .toList();

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching courses", HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> getEnrolledCourses(String studentEmail) {
        try {
            User student = userRepository.findByEmail(studentEmail)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

            List<Course> enrolledCourses = enrollments.stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());

            List<CourseDetailDto> courseDtos = enrolledCourses.stream()
                    .map(CourseMapper::toDetailDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching enrolled courses", HttpStatus.BAD_REQUEST);
        }
    }


}
