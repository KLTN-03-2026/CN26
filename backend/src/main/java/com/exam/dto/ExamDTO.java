package com.exam.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExamDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private String subject;
    private Integer totalQuestions;
    private BigDecimal passingScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private Integer createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
