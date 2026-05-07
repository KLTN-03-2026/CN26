package com.exam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    
    private String fullName;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 số và bắt đầu bằng số 0")
    private String phone;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String avatar;
}
