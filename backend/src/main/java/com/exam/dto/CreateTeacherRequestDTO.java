package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateTeacherRequestDTO {
    
    @NotBlank(message = "Lý do không được để trống")
    private String reason;
    
    private MultipartFile teachingCertificate;
    
    private MultipartFile degree;
}
