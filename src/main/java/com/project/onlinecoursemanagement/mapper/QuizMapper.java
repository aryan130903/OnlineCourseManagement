package com.project.onlinecoursemanagement.mapper;

import com.project.onlinecoursemanagement.dto.*;
import com.project.onlinecoursemanagement.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class QuizMapper {

    /**
     * Convert Quiz entity to QuizResponseDto
     */
    public static QuizResponseDto toResponseDto(Quiz quiz) {
        QuizResponseDto dto = new QuizResponseDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());

        // Map questions -> QuestionResponseDto
        List<QuestionResponseDto> questionDtos = quiz.getQuestions().stream()
                .map(QuizMapper::toQuestionResponseDto)
                .collect(Collectors.toList());
        dto.setQuestions(questionDtos);

        return dto;
    }

    /**
     * Convert Question entity -> QuestionResponseDto
     */
    public static QuestionResponseDto toQuestionResponseDto(Question question) {
        QuestionResponseDto dto = new QuestionResponseDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setOption1(question.getOption1());
        dto.setOption2(question.getOption2());
        dto.setOption3(question.getOption3());
        dto.setOption4(question.getOption4());
        return dto;
    }

    /**
     * Convert QuizRequestDto -> Quiz entity
     */
    public static Quiz toEntity(QuizRequestDto dto, Course course) {
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setCourse(course);
        return quiz;
    }

    /**
     * Convert QuestionRequestDto -> Question entity
     */
    public static Question toEntity(QuestionRequestDto dto, Quiz quiz) {
        Question question = new Question();
        question.setText(dto.getText());
        question.setOption1(dto.getOption1());
        question.setOption2(dto.getOption2());
        question.setOption3(dto.getOption3());
        question.setOption4(dto.getOption4());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setQuiz(quiz);
        return question;
    }
}
