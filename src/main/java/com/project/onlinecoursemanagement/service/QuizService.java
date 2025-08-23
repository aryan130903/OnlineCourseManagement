package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.QuestionRequestDto;
import com.project.onlinecoursemanagement.dto.QuestionResponseDto;
import com.project.onlinecoursemanagement.dto.QuizRequestDto;
import com.project.onlinecoursemanagement.dto.QuizResponseDto;
import com.project.onlinecoursemanagement.model.Question;
import com.project.onlinecoursemanagement.model.Quiz;

public interface QuizService {

    // Instructor: create quiz for a course
    Quiz createQuiz(Long courseId, QuizRequestDto dto, String email);

    // Instructor: add question to a quiz
    Question addQuestion(Long quizId, QuestionRequestDto dto, String email);

    // Student/Instructor: get quiz by course
    QuizResponseDto getQuizByCourse(Long courseId, String email);

    // Update quiz (instructor only)
    QuizRequestDto updateQuiz(Long quizId, QuizRequestDto dto);

    // Instructor: delete question from quiz
    void deleteQuestion(Long quizId,Long questionId, String email);
}
