package com.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewTeacherRequestDTO {
    
    @NotNull(message = "Trạng thái không được để trống")
    private String status; // approved, rejected
    
    private String reviewNote;
}
