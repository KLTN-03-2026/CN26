package com.exam.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResultDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer examId;
    private String examName;
    private BigDecimal score;
    private Integer totalCorrect;
    private Integer totalQuestions;
    private Integer timeSpent;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
}
