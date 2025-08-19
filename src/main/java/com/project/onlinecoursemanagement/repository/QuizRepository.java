package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByCourseId(Long courseId);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Quiz q WHERE q.id = :quizId")
//    void deleteQuizById(@Param("quizId") Long quizId);
}
