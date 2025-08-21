package com.project.onlinecoursemanagement.service.cloud;

import org.springframework.web.multipart.MultipartFile;

public interface CloudVideoUploadService {
    String uploadVideo(MultipartFile file) throws Exception;
}
