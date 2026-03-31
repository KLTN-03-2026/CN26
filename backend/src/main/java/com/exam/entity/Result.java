package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Result {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Exam exam;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    
    @Column(name = "total_correct")
    private Integer totalCorrect = 0;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    @Column(name = "time_spent")
    private Integer timeSpent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.in_progress;
    
    @Column(name = "start_time", updatable = false)
    @CreationTimestamp
    private LocalDateTime startTime;
    
    @Column(name = "submit_time")
    private LocalDateTime submitTime;
    
    public enum Status {
        in_progress, submitted, graded
    }
}
