package com.exam.repository;

import com.exam.entity.Exam;
import com.exam.entity.ExamQuestion;
import com.exam.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Integer> {
    
    List<ExamQuestion> findByExamOrderByQuestionOrder(Exam exam);
    
    List<ExamQuestion> findByExam(Exam exam);
    
    boolean existsByExamAndQuestion(Exam exam, Question question);
    
    void deleteByExam(Exam exam);
    
    long countByExam(Exam exam);
}
