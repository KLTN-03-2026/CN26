package com.exam.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private Boolean emailVerified;
    private String avatar;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
