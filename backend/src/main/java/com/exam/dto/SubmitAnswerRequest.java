package com.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerRequest {
    
    @NotNull(message = "ID kết quả không được để trống")
    private Integer resultId;
    
    @NotNull(message = "Danh sách câu trả lời không được để trống")
    private List<AnswerItem> answers;
    
    @Data
    public static class AnswerItem {
        @NotNull(message = "ID câu hỏi không được để trống")
        private Integer questionId;
        
        @NotNull(message = "Đáp án không được để trống")
        private String selectedAnswer; // A, B, C, D
    }
}
