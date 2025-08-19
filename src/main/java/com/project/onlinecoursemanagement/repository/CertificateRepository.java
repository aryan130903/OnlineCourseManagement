package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByStudentEmailAndCourseName(String studentEmail, String courseName);

    List<Certificate> findAllByStudentEmail(String studentEmail);

}
