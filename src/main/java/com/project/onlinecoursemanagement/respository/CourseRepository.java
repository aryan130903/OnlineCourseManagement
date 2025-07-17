package com.project.onlinecoursemanagement.respository;

import com.project.onlinecoursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,Integer> {

    List<Course> findByCategory_NameIgnoreCase(String name);

    List<Course> findByInstructorId(Integer instructorId);


}
