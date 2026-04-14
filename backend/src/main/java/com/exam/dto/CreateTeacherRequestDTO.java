package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeacherRequestDTO {
    
    @NotBlank(message = "Lý do không được để trống")
    private String reason;
    
    private String qualifications;
}
