package com.project.onlinecoursemanagement.service;

import com.cloudinary.Cloudinary;
import com.project.onlinecoursemanagement.config.CloudMediaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryVideoUploadService implements VideoUploadService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryVideoUploadService(CloudMediaClient mediaClient) {
        this.cloudinary = (Cloudinary) mediaClient.getClient();
    }

    @Override
    public String uploadVideo(MultipartFile file) throws Exception {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                "resource_type", "video"
        ));
        return uploadResult.get("secure_url").toString();
    }
}
