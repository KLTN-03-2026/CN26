package com.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherRequestDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String reason;
    private String qualifications;
    private String status;
    private Integer reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String reviewNote;
    private LocalDateTime createdAt;
}
