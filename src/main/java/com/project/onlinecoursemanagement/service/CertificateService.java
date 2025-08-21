package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.CertificateDto;

import java.util.List;

public interface CertificateService {

    String generateCertificate(String studentName, String studentEmail, String courseName, double score) throws Exception;

    List<CertificateDto> getCertificatesForStudent(String studentEmail);
}
