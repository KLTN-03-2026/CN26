package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.CreateTeacherRequestDTO;
import com.exam.dto.ReviewTeacherRequestDTO;
import com.exam.dto.TeacherRequestDTO;
import com.exam.service.TeacherRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-requests")
public class TeacherRequestController {
    
    @Autowired
    private TeacherRequestService teacherRequestService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherRequestDTO>> createRequest(
            @Valid @RequestBody CreateTeacherRequestDTO request) {
        TeacherRequestDTO teacherRequest = teacherRequestService.createRequest(request);
        return ResponseEntity.ok(ApiResponse.success("Gửi yêu cầu thành công", teacherRequest));
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TeacherRequestDTO>>> getMyRequests() {
        List<TeacherRequestDTO> requests = teacherRequestService.getMyRequests();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TeacherRequestDTO>>> getAllRequests() {
        List<TeacherRequestDTO> requests = teacherRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TeacherRequestDTO>>> getPendingRequests() {
        List<TeacherRequestDTO> requests = teacherRequestService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherRequestDTO>> getRequestById(@PathVariable Integer id) {
        TeacherRequestDTO request = teacherRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request));
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherRequestDTO>> reviewRequest(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewTeacherRequestDTO reviewDTO) {
        TeacherRequestDTO request = teacherRequestService.reviewRequest(id, reviewDTO);
        return ResponseEntity.ok(ApiResponse.success("Xử lý yêu cầu thành công", request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRequest(@PathVariable Integer id) {
        teacherRequestService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa yêu cầu thành công", null));
    }
    
    @GetMapping("/count/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countPendingRequests() {
        long count = teacherRequestService.countPendingRequests();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
