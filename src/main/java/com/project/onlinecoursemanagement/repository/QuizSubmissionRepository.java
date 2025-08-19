package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Quiz;
import com.project.onlinecoursemanagement.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByStudentEmailAndQuizId(String studentEmail, Long quizId);

//    void deleteByQuiz(Quiz quiz);
}


