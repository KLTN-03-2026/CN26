package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user", "exam"})
    private Result result;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdBy"})
    private Question question;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_answer", nullable = true)
    private Question.Answer selectedAnswer;
    
    // Getter tùy chỉnh để tránh lỗi khi serialize null
    public String getSelectedAnswerString() {
        return selectedAnswer != null ? selectedAnswer.name() : null;
    }
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    @Column(name = "answered_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime answeredAt;
}
