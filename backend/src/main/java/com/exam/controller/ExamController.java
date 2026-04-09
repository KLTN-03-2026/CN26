package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.CreateExamRequest;
import com.exam.dto.ExamDTO;
import com.exam.entity.Question;
import com.exam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getAllExams() {
        List<ExamDTO> exams = examService.getAllExams();
        return ResponseEntity.ok(ApiResponse.success(exams));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getActiveExams() {
        List<ExamDTO> exams = examService.getActiveExams();
        return ResponseEntity.ok(ApiResponse.success(exams));
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getMyExams() {
        List<ExamDTO> exams = examService.getMyExams();
        return ResponseEntity.ok(ApiResponse.success(exams));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDTO>> getExamById(@PathVariable Integer id) {
        ExamDTO exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success(exam));
    }
    
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<Question>>> getExamQuestions(@PathVariable Integer id) {
        List<Question> questions = examService.getExamQuestions(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
    
    @GetMapping("/subject/{subject}")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getExamsBySubject(@PathVariable String subject) {
        List<ExamDTO> exams = examService.getExamsBySubject(subject);
        return ResponseEntity.ok(ApiResponse.success(exams));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamDTO>> createExam(@Valid @RequestBody CreateExamRequest request) {
        ExamDTO exam = examService.createExam(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo đề thi thành công", exam));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamDTO>> updateExam(
            @PathVariable Integer id,
            @Valid @RequestBody CreateExamRequest request) {
        ExamDTO exam = examService.updateExam(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đề thi thành công", exam));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteExam(@PathVariable Integer id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đề thi thành công", null));
    }
    
    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> toggleExamStatus(@PathVariable Integer id) {
        examService.toggleExamStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Thay đổi trạng thái đề thi thành công", null));
    }
}
