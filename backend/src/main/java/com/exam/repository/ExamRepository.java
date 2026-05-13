package com.exam.repository;

import com.exam.entity.Exam;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    
    List<Exam> findAllByOrderByCreatedAtDesc();
    
    List<Exam> findByCreatedByOrderByCreatedAtDesc(User user);
    
    List<Exam> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
    
    List<Exam> findBySubjectOrderByCreatedAtDesc(String subject);
}
