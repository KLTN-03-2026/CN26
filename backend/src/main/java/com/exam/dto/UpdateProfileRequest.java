package com.exam.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    
    private String fullName;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String avatar;
}
