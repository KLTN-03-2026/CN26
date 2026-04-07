package com.exam.repository;

import com.exam.entity.Exam;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    
    List<Exam> findByCreatedBy(User user);
    
    List<Exam> findByIsActive(Boolean isActive);
    
    List<Exam> findBySubject(String subject);
    
    List<Exam> findByCreatedByAndIsActive(User user, Boolean isActive);
    
    long countByCreatedBy(User user);
}
