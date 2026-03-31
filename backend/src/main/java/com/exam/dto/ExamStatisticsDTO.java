package com.exam.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamStatisticsDTO {
    private Integer examId;
    private String examName;
    private String examDescription;
    private Long totalAttempts;
    private Long totalStudents;
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;
    private Long passedCount;
    private BigDecimal passRate;
    private List<StudentResult> results;
    private List<QuestionAnalysis> questionAnalysis;
    
    @Data
    public static class StudentResult {
        private Integer id;
        private String studentName;
        private String studentEmail;
        private BigDecimal score;
        private Integer correctAnswers;
        private Integer totalQuestions;
        private Integer timeSpent;
        private LocalDateTime submitTime;
    }
    
    @Data
    public static class QuestionAnalysis {
        private Integer questionId;
        private String content;
        private String correctAnswer;
        private Integer totalAnswers;
        private Integer correctCount;
        private BigDecimal correctPercentage;
    }
}
