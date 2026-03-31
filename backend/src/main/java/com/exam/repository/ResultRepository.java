package com.exam.repository;

import com.exam.entity.Exam;
import com.exam.entity.Result;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    
    List<Result> findByUser(User user);
    
    List<Result> findByExam(Exam exam);
    
    List<Result> findByUserAndExam(User user, Exam exam);
    
    Optional<Result> findByUserAndExamAndStatus(User user, Exam exam, Result.Status status);
    
    List<Result> findByUserAndStatus(User user, Result.Status status);
    
    List<Result> findByExamAndStatus(Exam exam, Result.Status status);
    
    long countByExam(Exam exam);
    
    long countByExamAndStatus(Exam exam, Result.Status status);
    
    // Statistics queries
    @Query("SELECT AVG(r.score) FROM Result r WHERE r.exam = :exam AND r.status = 'graded'")
    BigDecimal findAverageScoreByExam(@Param("exam") Exam exam);
    
    @Query("SELECT MAX(r.score) FROM Result r WHERE r.exam = :exam AND r.status = 'graded'")
    BigDecimal findMaxScoreByExam(@Param("exam") Exam exam);
    
    @Query("SELECT MIN(r.score) FROM Result r WHERE r.exam = :exam AND r.status = 'graded'")
    BigDecimal findMinScoreByExam(@Param("exam") Exam exam);
}
