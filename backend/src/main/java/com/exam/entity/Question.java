package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "option_a", nullable = false, length = 500)
    private String optionA;
    
    @Column(name = "option_b", nullable = false, length = 500)
    private String optionB;
    
    @Column(name = "option_c", nullable = false, length = 500)
    private String optionC;
    
    @Column(name = "option_d", nullable = false, length = 500)
    private String optionD;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "correct_answer", nullable = false)
    private Answer correctAnswer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level = Level.medium;
    
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum Answer {
        A, B, C, D
    }
    
    public enum Level {
        easy, medium, hard
    }
}
