package com.exam.repository;

import com.exam.entity.Question;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    List<Question> findByCreatedBy(User user);
    
    List<Question> findBySubject(String subject);
    
    List<Question> findByLevel(Question.Level level);
    
    List<Question> findBySubjectAndLevel(String subject, Question.Level level);
    
    long countByCreatedBy(User user);
    
    long countBySubject(String subject);
}
