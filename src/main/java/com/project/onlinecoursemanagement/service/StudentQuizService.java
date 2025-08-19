package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.AnswerDto;
import com.project.onlinecoursemanagement.dto.QuizResponseDto;
import com.project.onlinecoursemanagement.dto.QuizResultDto;
import com.project.onlinecoursemanagement.dto.QuizSubmissionDto;
import com.project.onlinecoursemanagement.exception.*;
import com.project.onlinecoursemanagement.model.*;
import com.project.onlinecoursemanagement.mapper.QuizMapper;
import com.project.onlinecoursemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentQuizService {

    private final CourseRepository courseRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final VideoLectureRepository videoLectureRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final CertificateService certificateService;
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;

//    private final QuizMapper quizMapper; // helper mapper for DTOs

    /**
     * Fetch quiz for a course.
     * Only if student has completed all lectures.
     */
    @Transactional(readOnly = true)
    public QuizResponseDto getQuizForStudent(Long courseId, String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        // check student exists
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!enrolled) {
            throw new UnauthorizedAccessException("You are not enrolled in this course.");
        }


        // check all lectures watched
        if (!checkAllLecturesWatched(courseId, studentEmail)) {
            throw new LectureNotWatchedException("You must watch all lectures before attempting the quiz.");
        }

        Quiz quiz = quizRepository.findByCourseId(courseId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found for this course"));

        return QuizMapper.toResponseDto(quiz);
    }

    /**
     * Submit quiz answers.
     */
    @Transactional
    public QuizResultDto submitQuiz(Long courseId, String studentEmail, QuizSubmissionDto submission) {
        // Fetch course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        // Fetch quiz for the course
        Quiz quiz = quizRepository.findByCourseId(courseId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found for this course"));

        int totalQuestions = quiz.getQuestions().size();
        int correctCount = 0;

        // Check each submitted answer
        for (AnswerDto answer : submission.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new QuestionNotFoundException("Question not found"));

            if (question.getCorrectAnswer().equalsIgnoreCase(answer.getSelectedAnswer())) {
                correctCount++;
            }
        }

        double scorePercentage = (totalQuestions == 0) ? 0.0 : (correctCount * 100.0 / totalQuestions);

        // Save quiz submission
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        QuizSubmission quizSubmission = QuizSubmission.builder()
                .quiz(quiz)
                .student(student)
                .score((int) scorePercentage)
                .submittedAt(LocalDateTime.now())
                .build();

        quizSubmissionRepository.save(quizSubmission);

        // Prepare result DTO
        QuizResultDto result = new QuizResultDto();
        result.setQuizId(quiz.getId());
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctCount);
        result.setScorePercentage(scorePercentage);

        // Set message based on score
        if (scorePercentage >= 80) {
            result.setMessage("Good job! You scored more than 80%");
        } else {
            result.setMessage("Sorry, try again. Keep practicing!");
        }


        // Generate certificate if score >= 80%
        if (scorePercentage >= 80) {
            try {
                // Check if certificate already exists
                Certificate certificate = certificateRepository
                        .findByStudentEmailAndCourseName(student.getEmail(), course.getTitle())
                        .orElse(null);

                String certificateUrl = certificateService.generateCertificate(
                        student.getUsername(),
                        student.getEmail(),
                        course.getTitle(),
                        scorePercentage
                );

                if (certificate != null) {
                    // Update existing certificate
                    certificate.setFilePath(certificateUrl);
                    certificate.setIssueDate(LocalDate.now());
                    certificateRepository.save(certificate);
                } else {
                    // Create new certificate
                    Certificate newCertificate = new Certificate();
                    newCertificate.setStudentEmail(student.getEmail());
                    newCertificate.setCourseName(course.getTitle());
                    newCertificate.setFilePath(certificateUrl);
                    newCertificate.setIssueDate(LocalDate.now());
                    certificateRepository.save(newCertificate);
                }

                // Add certificate download URL to result
                result.setCertificateUrl(certificateUrl);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }




    /**
     * Mark a video lecture as watched.
     */
    @Transactional
    public String markLectureWatched(Long courseId, Long lectureId, String studentEmail) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        // check student exists
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!enrolled) {
            throw new UnauthorizedAccessException("You are not enrolled in this course.");
        }


        VideoLecture lecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() -> new VideoLectureNotFoundException("Lecture not found"));

        if (!lecture.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lecture does not belong to this course");
        }

        // check if already marked
        boolean exists = videoProgressRepository.existsByStudentEmailAndVideoLecture_Id(studentEmail, lectureId);
        if (exists) {
            return "Lecture already marked as watched.";
        }

        VideoProgress progress = new VideoProgress();
        progress.setStudentEmail(studentEmail);
        progress.setVideoLecture(lecture);

        videoProgressRepository.save(progress);

        return "Lecture marked as watched.";
    }

    /**
     * Check if all lectures are completed for a course.
     */
    @Transactional(readOnly = true)
    public boolean checkAllLecturesWatched(Long courseId, String studentEmail) {
        List<VideoLecture> lectures = videoLectureRepository.findByCourseId(courseId);

        if (lectures.isEmpty()) {
            return true; // no lectures, consider completed
        }

        List<VideoProgress> progress = videoProgressRepository.findByStudentEmailAndVideoLecture_Course_Id(studentEmail, courseId);

        System.out.println(lectures.size());
        System.out.println(progress.size());

        return  progress.size() == lectures.size();
    }
}

