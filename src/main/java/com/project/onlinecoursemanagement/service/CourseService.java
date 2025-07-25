package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CourseDetailDto;
import com.project.onlinecoursemanagement.dto.CourseDto;
import com.project.onlinecoursemanagement.dto.CourseSummaryDto;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
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


//    public ResponseEntity<?> getCourseById(Long id) {
//        try {
//            Optional<Course> course = courseRepository.findById(id);
//            if (course.isPresent()) {
//                CourseDto courseDto = CourseMapper.toDto(course.get());
//                return new ResponseEntity<>(courseDto, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
//        }
//    }

    public ResponseEntity<?> getCourseById(Long courseId, String studentEmail) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            User student = userRepository.findByEmail(studentEmail)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

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


    public ResponseEntity<String> addCourse(Course course) {
        try {
            courseRepository.save(course);
            return new ResponseEntity<>("Success",HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failure", HttpStatus.BAD_REQUEST);
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


    public ResponseEntity<String> updateCourse(Long courseId, Course updatedCourseData) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        Course existingCourse = optionalCourse.get();

        if (updatedCourseData.getTitle() != null)
            existingCourse.setTitle(updatedCourseData.getTitle());

        if (updatedCourseData.getDescription() != null)
            existingCourse.setDescription(updatedCourseData.getDescription());

        if (updatedCourseData.getPrice() != null)
            existingCourse.setPrice(updatedCourseData.getPrice());

        if (updatedCourseData.getCategory() != null)
            existingCourse.setCategory(updatedCourseData.getCategory());

        courseRepository.save(existingCourse);
        return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<?> getAllCategory() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Unable to fetch categories", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getCoursesByInstructorUsername(String username) {
        try {
            User instructor = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Course> courses = courseRepository.findByInstructor(instructor);
            return new ResponseEntity<>(courses, HttpStatus.OK);
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
