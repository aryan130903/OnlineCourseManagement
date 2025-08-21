package com.project.onlinecoursemanagement.service;

import com.project.onlinecoursemanagement.dto.QuizResponseDto;
import com.project.onlinecoursemanagement.dto.QuizResultDto;
import com.project.onlinecoursemanagement.dto.QuizSubmissionDto;

public interface StudentQuizService {

    // Fetch quiz for a course (only if student has completed all lectures)
    QuizResponseDto getQuizForStudent(Long courseId, String studentEmail);

    // Submit quiz answers
    QuizResultDto submitQuiz(Long courseId, String studentEmail, QuizSubmissionDto submission);

    // Mark a video lecture as watched
    String markLectureWatched(Long courseId, Long lectureId, String studentEmail);

    // Check if all lectures are completed for a course
    boolean checkAllLecturesWatched(Long courseId, String studentEmail);
}
