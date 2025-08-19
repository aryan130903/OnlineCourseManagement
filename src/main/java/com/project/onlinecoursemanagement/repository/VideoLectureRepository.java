package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.VideoLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoLectureRepository extends JpaRepository<VideoLecture, Long> {


    List<VideoLecture> findByCourseId(Long courseId);

    boolean existsByCourseIdAndTitle(Long courseId, String title);
}

