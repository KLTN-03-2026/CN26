package com.exam.repository;

import com.exam.entity.Question;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    List<Question> findByCreatedBy(User user);
    
    List<Question> findBySubject(String subject);
    
    List<Question> findByLevel(Question.Level level);
    
    @Query("SELECT q FROM Question q WHERE q.createdBy = :user " +
           "AND (:source IS NULL OR q.source = :source) " +
           "ORDER BY CASE WHEN :sortOrder = 'newest' THEN q.createdAt END DESC, " +
           "CASE WHEN :sortOrder = 'oldest' THEN q.createdAt END ASC")
    List<Question> findByCreatedByWithFilters(
        @Param("user") User user,
        @Param("source") Question.Source source,
        @Param("sortOrder") String sortOrder
    );
}
