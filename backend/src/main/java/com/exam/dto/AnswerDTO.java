package com.exam.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Integer id;
    private Integer questionId;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String selectedAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
