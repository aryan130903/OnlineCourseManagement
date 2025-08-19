package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.User;
import com.project.onlinecoursemanagement.model.VideoLecture;
import com.project.onlinecoursemanagement.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

//    @Query("SELECT vp FROM VideoProgress vp WHERE vp.studentEmail = :email AND vp.videoLecture.course.id = :courseId AND vp.watched = true")
//    List<VideoProgress> findWatchedByStudentAndCourse(@Param("email") String email, @Param("courseId") Long courseId);


    boolean existsByStudentEmailAndVideoLecture_Id(String studentEmail, Long lectureId);

    List<VideoProgress> findByStudentEmailAndVideoLecture_Course_Id(String studentEmail, Long courseId);

//    Optional<VideoProgress> findByStudentEmailAndVideoLecture_Id(String studentEmail, Long lectureId);

}

