package com.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionDTO {
    private Integer id;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String level;
    private String subject;
    private String explanation;
    private Integer createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
