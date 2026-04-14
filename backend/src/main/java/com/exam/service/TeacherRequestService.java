package com.exam.service;

import com.exam.dto.CreateTeacherRequestDTO;
import com.exam.dto.ReviewTeacherRequestDTO;
import com.exam.dto.TeacherRequestDTO;
import com.exam.entity.TeacherRequest;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.TeacherRequestRepository;
import com.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherRequestService {
    
    @Autowired
    private TeacherRequestRepository teacherRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public TeacherRequestDTO createRequest(CreateTeacherRequestDTO request) {
        User currentUser = userService.getCurrentUser();
        
        // Check if user is already a teacher
        if (currentUser.getRole() == User.Role.teacher || currentUser.getRole() == User.Role.admin) {
            throw new BadRequestException("Bạn đã là giáo viên hoặc quản trị viên");
        }
        
        // Check if user already has a pending request
        if (teacherRequestRepository.existsByUserAndStatus(currentUser, TeacherRequest.Status.pending)) {
            throw new BadRequestException("Bạn đã có yêu cầu đang chờ duyệt");
        }
        
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setUser(currentUser);
        teacherRequest.setReason(request.getReason());
        teacherRequest.setQualifications(request.getQualifications());
        teacherRequest.setStatus(TeacherRequest.Status.pending);
        
        teacherRequest = teacherRequestRepository.save(teacherRequest);
        return convertToDTO(teacherRequest);
    }
    
    public List<TeacherRequestDTO> getAllRequests() {
        return teacherRequestRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<TeacherRequestDTO> getPendingRequests() {
        return teacherRequestRepository.findByStatus(TeacherRequest.Status.pending).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<TeacherRequestDTO> getMyRequests() {
        User currentUser = userService.getCurrentUser();
        return teacherRequestRepository.findByUser(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public TeacherRequestDTO getRequestById(Integer id) {
        TeacherRequest request = teacherRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu không tồn tại"));
        return convertToDTO(request);
    }
    
    @Transactional
    public TeacherRequestDTO reviewRequest(Integer id, ReviewTeacherRequestDTO reviewDTO) {
        TeacherRequest request = teacherRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu không tồn tại"));
        
        if (request.getStatus() != TeacherRequest.Status.pending) {
            throw new BadRequestException("Yêu cầu đã được xử lý");
        }
        
        User admin = userService.getCurrentUser();
        TeacherRequest.Status newStatus = TeacherRequest.Status.valueOf(reviewDTO.getStatus());
        
        request.setStatus(newStatus);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewNote(reviewDTO.getReviewNote());
        
        // If approved, update user role
        if (newStatus == TeacherRequest.Status.approved) {
            User user = request.getUser();
            user.setRole(User.Role.teacher);
            userRepository.save(user);
            
            // Send approval email
            emailService.sendTeacherRequestApprovalEmail(user.getEmail(), user.getFullName());
        } else if (newStatus == TeacherRequest.Status.rejected) {
            // Send rejection email
            emailService.sendTeacherRequestRejectionEmail(
                    request.getUser().getEmail(),
                    request.getUser().getFullName(),
                    reviewDTO.getReviewNote() != null ? reviewDTO.getReviewNote() : "Không đáp ứng yêu cầu"
            );
        }
        
        request = teacherRequestRepository.save(request);
        return convertToDTO(request);
    }
    
    @Transactional
    public void deleteRequest(Integer id) {
        TeacherRequest request = teacherRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!request.getUser().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền xóa yêu cầu này");
        }
        
        teacherRequestRepository.deleteById(id);
    }
    
    public long countPendingRequests() {
        return teacherRequestRepository.countByStatus(TeacherRequest.Status.pending);
    }
    
    private TeacherRequestDTO convertToDTO(TeacherRequest request) {
        TeacherRequestDTO dto = new TeacherRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUser().getId());
        dto.setUserName(request.getUser().getFullName());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setReason(request.getReason());
        dto.setQualifications(request.getQualifications());
        dto.setStatus(request.getStatus().name());
        
        if (request.getReviewedBy() != null) {
            dto.setReviewedById(request.getReviewedBy().getId());
            dto.setReviewedByName(request.getReviewedBy().getFullName());
        }
        
        dto.setReviewedAt(request.getReviewedAt());
        dto.setReviewNote(request.getReviewNote());
        dto.setCreatedAt(request.getCreatedAt());
        
        return dto;
    }
}
