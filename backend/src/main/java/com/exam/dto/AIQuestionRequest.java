package com.exam.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AIQuestionRequest {
    
    @NotBlank(message = "Chủ đề không được để trống")
    private String topic;
    
    @Min(value = 1, message = "Số lượng câu hỏi phải từ 1 đến 20")
    @Max(value = 20, message = "Số lượng câu hỏi phải từ 1 đến 20")
    private Integer quantity = 5;
    
    private String level = "medium"; // easy, medium, hard
    
    private String subject;
}
