package com.exam.repository;

import com.exam.entity.Answer;
import com.exam.entity.Question;
import com.exam.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    
    List<Answer> findByResult(Result result);
    
    List<Answer> findByResultAndQuestion(Result result, Question question);
    
    long countByResultAndIsCorrect(Result result, Boolean isCorrect);
    
    void deleteByResult(Result result);
}
