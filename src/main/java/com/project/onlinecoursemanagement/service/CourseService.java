package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.exception.*;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.*;
import com.project.onlinecoursemanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    CourseRepository courseRepository;

    CategoryRepository categoryRepository;

    UserRepository userRepository;

    EnrollmentRepository enrollmentRepository;

    CartRepository cartRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,UserRepository userRepository,EnrollmentRepository enrollmentRepository,CartRepository cartRepository){
        this.courseRepository=courseRepository;
        this.categoryRepository=categoryRepository;
        this.userRepository=userRepository;
        this.enrollmentRepository=enrollmentRepository;
        this.cartRepository=cartRepository;
    }

    public ResponseEntity<?> getAllCourses() {

        List<Course> courses = courseRepository.findAll();

        if (courses.isEmpty()) {
            throw new CourseNotFoundException("No courses exists");
        }

        List<CourseDto> courseDtos = courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(courseDtos, HttpStatus.OK);

    }


    public ResponseEntity<?> getCourseById(Long courseId, String studentEmail) {
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

    }




    public ResponseEntity<?> getCoursesByInstructor(Integer id) {

            List<Course> courses = courseRepository.findByInstructorId(id);
            if (courses.isEmpty()) {
                throw new CourseNotFoundException("No courses exists for this Instructor");
            }
            List<CourseDto> courseDtos = courses.stream()
                    .map(CourseMapper::toDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(courseDtos, HttpStatus.OK);

    }


    public ResponseEntity<?> getCourseByCategory(String category) {

        if (!categoryRepository.existsByName(category.toLowerCase())) {
            throw new CourseNotFoundException("Category '" + category + "' does not exist");
        }

        List<Course> courses = courseRepository.findByCategory_NameIgnoreCase(category);

        if (courses.isEmpty()) {
            throw new CourseNotFoundException("No courses found in category: " + category);
        }

        List<CourseDto> courseDtos = courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(courseDtos, HttpStatus.OK);
    }


    public List<CourseSummaryDto> searchCoursesByTitle(String title) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(title);

        if (courses.isEmpty()) {
            throw new CourseNotFoundException("No courses found with title containing: " + title);
        }

        return courses.stream()
                .map(CourseMapper::toSummaryDto)
                .toList();
    }


    public ResponseEntity<String> addCourse(CourseRequestDto dto,String email) {
        Optional<Course> existingCourse = courseRepository.findByTitleAndInstructorEmail(dto.getTitle(), email);

        if (existingCourse.isPresent()) {
            return new ResponseEntity<>("Course with the same title already exists for this instructor", HttpStatus.CONFLICT);
        }

        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        User instructor = userRepository.findByEmail(email)
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
    @Transactional
    public ResponseEntity<String> deleteCourse(Long id, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not allowed to delete this course");
        }

        // Remove course from all carts
        List<Cart> cartsWithCourse = cartRepository.findAllByCourses_Id(id);
        for (Cart cart : cartsWithCourse) {
            cart.getCourses().remove(course);
        }
        cartRepository.saveAll(cartsWithCourse);

        // Delete course
        courseRepository.delete(course);

        return ResponseEntity.ok("Course deleted successfully");
    }





    public ResponseEntity<String> updateCourse(Long courseId, CourseRequestDto dto,String email) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        Course existingCourse = optionalCourse.get();

        if (!existingCourse.getInstructor().getEmail().equals(email)) {
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

//        if (dto.getVideoLectures() != null) {
//            Map<Long, VideoLecture> existingVideos = existingCourse.getVideoLectures()
//                    .stream()
//                    .filter(v -> v.getId() != null)
//                    .collect(Collectors.toMap(VideoLecture::getId, v -> v));
//
//            for (VideoLectureDto videoDto : dto.getVideoLectures()) {
//                if (videoDto.getId() != null && existingVideos.containsKey(videoDto.getId())) {
//                    // Update existing video
//                    VideoLecture video = existingVideos.get(videoDto.getId());
//                    video.setTitle(videoDto.getTitle());
//                    video.setVideoUrl(videoDto.getVideoUrl());
//                } else {
//                    // Add new video
//                    VideoLecture newVideo = new VideoLecture();
//                    newVideo.setTitle(videoDto.getTitle());
//                    newVideo.setVideoUrl(videoDto.getVideoUrl());
//                    newVideo.setCourse(existingCourse);
//                    existingCourse.getVideoLectures().add(newVideo);
//                }
//            }
//        }

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

    public CourseDetailDto getInstructorCourseById(Long courseId, String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not authorized to view this course");
        }

        return CourseMapper.toDetailDto(course);
    }

    public ResponseEntity<String> deleteVideoLecture(Long courseId, Long videoId, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new UnauthorizedAccessException("You are not authorized to delete video lectures from this course.");
        }

        List<VideoLecture> lectures = course.getVideoLectures();

        boolean removed = lectures.removeIf(video -> video.getId().equals(videoId));
        if (!removed) {
            throw new VideoLectureNotFoundException("Video lecture not found with ID: " + videoId);
        }
        courseRepository.save(course);

        return ResponseEntity.ok("Video lecture deleted successfully");
    }




    public List<CourseDetailDto> getEnrolledCourses(String studentEmail) {
        try {
            User student = userRepository.findByEmail(studentEmail)
                    .orElseThrow(() -> new UserNotFoundException("Student not found"));

            List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

            List<Course> enrolledCourses = enrollments.stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());

            List<CourseDetailDto> courseDtos = enrolledCourses.stream()
                    .map(CourseMapper::toDetailDto)
                    .collect(Collectors.toList());

//            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
           return courseDtos;
        } catch (Exception e) {
            e.printStackTrace();
//            return new ResponseEntity<>("Error fetching enrolled courses", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("Error fetching enrolled courses");

        }
    }


}
