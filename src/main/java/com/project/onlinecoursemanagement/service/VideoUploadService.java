package com.project.onlinecoursemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface VideoUploadService {
    String uploadVideo(MultipartFile file) throws Exception;
}
