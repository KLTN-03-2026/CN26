package com.exam.repository;

import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByVerificationToken(String token);
    
    Optional<User> findByResetToken(String token);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByStatus(User.Status status);
    
    long countByRole(User.Role role);
}
