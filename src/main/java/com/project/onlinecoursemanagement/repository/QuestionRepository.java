package com.project.onlinecoursemanagement.repository;

import com.project.onlinecoursemanagement.model.Question;
import com.project.onlinecoursemanagement.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long quizId);

    boolean existsByQuizAndText(Quiz quiz, String text);

//    void deleteByQuiz(Quiz quiz);
}

