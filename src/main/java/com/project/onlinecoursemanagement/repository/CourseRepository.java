package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,Long> {

    List<Course> findByCategory_NameIgnoreCase(String name);

    List<Course> findByInstructorId(Integer instructorId);

    List<Course> findByInstructor(User instructor);



}
