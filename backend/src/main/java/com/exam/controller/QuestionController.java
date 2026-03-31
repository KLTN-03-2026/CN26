package com.exam.controller;

import com.exam.dto.AIQuestionRequest;
import com.exam.dto.ApiResponse;
import com.exam.dto.CreateQuestionRequest;
import com.exam.dto.QuestionDTO;
import com.exam.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getAllQuestions() {
        List<QuestionDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getMyQuestions() {
        List<QuestionDTO> questions = questionService.getMyQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuestionDTO>> getQuestionById(@PathVariable Integer id) {
        QuestionDTO question = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(question));
    }
    
    @GetMapping("/subject/{subject}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getQuestionsBySubject(@PathVariable String subject) {
        List<QuestionDTO> questions = questionService.getQuestionsBySubject(subject);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
    
    @GetMapping("/level/{level}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> getQuestionsByLevel(@PathVariable String level) {
        List<QuestionDTO> questions = questionService.getQuestionsByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuestionDTO>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionDTO question = questionService.createQuestion(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo câu hỏi thành công", question));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuestionDTO>> updateQuestion(
            @PathVariable Integer id,
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionDTO question = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật câu hỏi thành công", question));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa câu hỏi thành công", null));
    }
    
    @PostMapping("/generate-ai")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> generateQuestionsWithAI(
            @Valid @RequestBody AIQuestionRequest request) {
        List<QuestionDTO> questions = questionService.generateQuestionsWithAI(request);
        return ResponseEntity.ok(ApiResponse.success("Sinh câu hỏi bằng AI thành công", questions));
    }
    
    @PostMapping("/import-word")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> importQuestionsFromWord(
            @RequestParam("file") MultipartFile file) {
        List<QuestionDTO> questions = questionService.importQuestionsFromWord(file);
        return ResponseEntity.ok(ApiResponse.success("Import câu hỏi từ Word thành công", questions));
    }
}
