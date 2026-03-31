package com.exam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExamRequest {
    
    @NotBlank(message = "Tên đề thi không được để trống")
    private String name;
    
    private String description;
    
    @NotNull(message = "Thời gian làm bài không được để trống")
    @Min(value = 1, message = "Thời gian làm bài phải lớn hơn 0")
    private Integer duration;
    
    private String subject;
    
    private BigDecimal passingScore = new BigDecimal("50.00");
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @NotNull(message = "Danh sách câu hỏi không được để trống")
    private List<Integer> questionIds;
}
