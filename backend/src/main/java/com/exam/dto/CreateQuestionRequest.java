package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuestionRequest {
    
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;
    
    @NotBlank(message = "Đáp án A không được để trống")
    private String optionA;
    
    @NotBlank(message = "Đáp án B không được để trống")
    private String optionB;
    
    @NotBlank(message = "Đáp án C không được để trống")
    private String optionC;
    
    @NotBlank(message = "Đáp án D không được để trống")
    private String optionD;
    
    @NotNull(message = "Đáp án đúng không được để trống")
    private String correctAnswer; // A, B, C, D
    
    @NotNull(message = "Độ khó không được để trống")
    private String level; // easy, medium, hard
    
    private String subject;
    private String explanation;
}
