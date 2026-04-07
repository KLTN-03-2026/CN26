package com.exam.repository;

import com.exam.entity.TeacherRequest;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRequestRepository extends JpaRepository<TeacherRequest, Integer> {
    
    List<TeacherRequest> findByStatus(TeacherRequest.Status status);
    
    List<TeacherRequest> findByUser(User user);
    
    Optional<TeacherRequest> findByUserAndStatus(User user, TeacherRequest.Status status);
    
    boolean existsByUserAndStatus(User user, TeacherRequest.Status status);
    
    long countByStatus(TeacherRequest.Status status);
}
