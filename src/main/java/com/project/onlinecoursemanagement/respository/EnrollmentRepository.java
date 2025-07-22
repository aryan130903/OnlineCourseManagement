package com.project.onlinecoursemanagement.respository;

import com.project.onlinecoursemanagement.model.Enrollment;
import com.project.onlinecoursemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
}
