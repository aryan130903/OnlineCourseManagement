package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CourseDto;
import com.project.onlinecoursemanagement.mapper.CourseMapper;
import com.project.onlinecoursemanagement.model.Category;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.respository.CategoryRepository;
import com.project.onlinecoursemanagement.respository.CourseRepository;
import com.project.onlinecoursemanagement.respository.UserRepository;
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

    @Autowired
    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,UserRepository userRepository){
        this.courseRepository=courseRepository;
        this.categoryRepository=categoryRepository;
        this.userRepository=userRepository;
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


    public ResponseEntity<?> getCourseById(Integer id) {
        try {
            Optional<Course> course = courseRepository.findById(id);
            if (course.isPresent()) {
                CourseDto courseDto = CourseMapper.toDto(course.get());
                return new ResponseEntity<>(courseDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> deleteCourse(int id) {

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


    public ResponseEntity<String> updateCourse(Integer courseId, Course updatedCourseData) {
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

}
