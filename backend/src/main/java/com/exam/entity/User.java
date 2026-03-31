package com.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.student;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.inactive;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "verification_token")
    private String verificationToken;
    
    @Column(name = "reset_token")
    private String resetToken;
    
    private String avatar;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Additional fields for profile
    private String phone;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    public enum Role {
        student, teacher, admin
    }
    
    public enum Status {
        active, inactive, locked
    }
    
    public enum Gender {
        male, female, other
    }
}
