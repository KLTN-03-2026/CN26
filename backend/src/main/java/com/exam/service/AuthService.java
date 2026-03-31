package com.exam.service;

import com.exam.dto.AuthRequest;
import com.exam.dto.AuthResponse;
import com.exam.dto.RegisterRequest;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.UnauthorizedException;
import com.exam.repository.UserRepository;
import com.exam.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(User.Role.student);
        user.setStatus(User.Status.inactive); // Chờ xác thực email
        user.setEmailVerified(false); // Chưa xác thực
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        
        user = userRepository.save(user);
        
        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
        
        // Generate JWT token (user can still get token but can't login until verified)
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    @Transactional
    public AuthResponse login(AuthRequest request) {
        // Get user first to check status before authentication
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không đúng"));
        
        // Check account status BEFORE password check
        if (user.getStatus() == User.Status.inactive) {
            throw new UnauthorizedException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email");
        }
        
        if (user.getStatus() == User.Status.locked) {
            throw new UnauthorizedException("Tài khoản đã bị khóa");
        }
        
        // Now authenticate with password
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Email hoặc mật khẩu không đúng");
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Token xác thực không hợp lệ hoặc đã hết hạn"));
        
        user.setEmailVerified(true);
        user.setStatus(User.Status.active);
        user.setVerificationToken(null);
        
        userRepository.save(user);
    }
    
    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email không tồn tại"));
        
        // Check if already verified
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BadRequestException("Tài khoản đã được xác thực");
        }
        
        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(email, verificationToken);
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email không tồn tại"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(email, resetToken);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Token không hợp lệ"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
