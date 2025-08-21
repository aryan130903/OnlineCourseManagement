package com.project.onlinecoursemanagement.service.impl;

import com.project.onlinecoursemanagement.dto.QuestionRequestDto;
import com.project.onlinecoursemanagement.dto.QuestionResponseDto;
import com.project.onlinecoursemanagement.dto.QuizRequestDto;
import com.project.onlinecoursemanagement.dto.QuizResponseDto;
import com.project.onlinecoursemanagement.exception.*;
import com.project.onlinecoursemanagement.model.Course;
import com.project.onlinecoursemanagement.model.Question;
import com.project.onlinecoursemanagement.model.Quiz;
import com.project.onlinecoursemanagement.repository.CourseRepository;
import com.project.onlinecoursemanagement.repository.QuestionRepository;
import com.project.onlinecoursemanagement.repository.QuizRepository;
import com.project.onlinecoursemanagement.repository.QuizSubmissionRepository;
import com.project.onlinecoursemanagement.service.QuizService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    // Instructor: create quiz for a course
    public Quiz createQuiz(Long courseId, QuizRequestDto dto,String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You cannot modify this course");
        }


        if (course.getQuiz() != null) {
            throw new QuizAlreadyExistsException("Quiz already exists for this course");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setCourse(course);
        return quizRepository.save(quiz);
    }

    // Instructor: add question to a quiz
    public Question addQuestion(Long quizId, QuestionRequestDto dto,String email) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found"));

        if (!quiz.getCourse().getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You cannot modify this quiz");
        }

        boolean exists = questionRepository.existsByQuizAndText(quiz, dto.getText());
        if (exists) {
            throw new QuestionAlreadyExistsException("This question already exists in the quiz");
        }

        Question question = new Question();
        question.setQuiz(quiz);
        question.setText(dto.getText());
        question.setOption1(dto.getOption1());
        question.setOption2(dto.getOption2());
        question.setOption3(dto.getOption3());
        question.setOption4(dto.getOption4());
        question.setCorrectAnswer(dto.getCorrectAnswer());

        return questionRepository.save(question);
    }

    // Student/Instructor: get quiz by course
    public QuizResponseDto getQuizByCourse(Long courseId,String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not allowed to access this course");
        }

        Quiz quiz = course.getQuiz();
        if (quiz == null) {
            throw new QuizNotFoundException("Quiz not found for this course");
        }


        // Map Quiz entity to QuizResponseDto
        QuizResponseDto dto = new QuizResponseDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());

        List<QuestionResponseDto> questionDtos = quiz.getQuestions().stream().map(q -> {
            QuestionResponseDto qDto = new QuestionResponseDto();
            qDto.setId(q.getId());
            qDto.setText(q.getText());
            qDto.setOption1(q.getOption1());
            qDto.setOption2(q.getOption2());
            qDto.setOption3(q.getOption3());
            qDto.setOption4(q.getOption4());
            return qDto;
        }).toList();

        dto.setQuestions(questionDtos);

        return dto;
    }


    // Get all questions for a quiz
//    public List<Question> getQuestions(Long quizId) {
//        return questionRepository.findByQuizId(quizId);
//    }


//    @Transactional
//    public void deleteQuiz(Long quizId) {
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new QuizNotFoundException("Quiz not found"));
//
//        System.out.println(quiz.getId());
//
////        quizSubmissionRepository.deleteByQuiz(quiz);
////
//// delete questions for this quiz
//        questionRepository.deleteByQuiz(quiz);
//
//        // Then delete the quiz (questions will be removed automatically)
//         quizRepository.deleteQuizById(quizId);
//    }


    public QuizRequestDto updateQuiz(Long quizId, QuizRequestDto dto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found"));

        // Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        // ✅ Only the course's instructor can update
        if (!quiz.getCourse().getInstructor().getEmail().equals(loggedInEmail)) {
            throw new UnauthorizedAccessException("You are not allowed to update this quiz");
        }

        // ✅ Update allowed fields
        if (dto.getTitle() != null) {
            quiz.setTitle(dto.getTitle());
        }

         quizRepository.save(quiz);

        return dto;
    }



    @Transactional
    public void deleteQuestion(Long questionId,String email) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizNotFoundException("Question not found with ID: " + questionId));

        if (!question.getQuiz().getCourse().getInstructor().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not allowed to delete this question");
        }

        questionRepository.delete(question);
    }
}

