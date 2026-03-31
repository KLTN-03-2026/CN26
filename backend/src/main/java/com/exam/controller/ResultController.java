package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.ExamStatisticsDTO;
import com.exam.dto.ResultDTO;
import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.Answer;
import com.exam.service.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {
    
    @Autowired
    private ResultService resultService;
    
    @PostMapping("/start/{examId}")
    public ResponseEntity<ApiResponse<ResultDTO>> startExam(@PathVariable Integer examId) {
        ResultDTO result = resultService.startExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Bắt đầu làm bài thi", result));
    }
    
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ResultDTO>> submitExam(@Valid @RequestBody SubmitAnswerRequest request) {
        ResultDTO result = resultService.submitExam(request);
        return ResponseEntity.ok(ApiResponse.success("Nộp bài thành công", result));
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ResultDTO>>> getMyResults() {
        List<ResultDTO> results = resultService.getMyResults();
        return ResponseEntity.ok(ApiResponse.success(results));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResultDTO>> getResultById(@PathVariable Integer id) {
        ResultDTO result = resultService.getResultById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @GetMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<List<Answer>>> getResultAnswers(@PathVariable Integer id) {
        List<Answer> answers = resultService.getResultAnswers(id);
        return ResponseEntity.ok(ApiResponse.success(answers));
    }
    
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ResultDTO>>> getExamResults(@PathVariable Integer examId) {
        List<ResultDTO> results = resultService.getExamResults(examId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
    
    @GetMapping("/exam/{examId}/statistics")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamStatisticsDTO>> getExamStatistics(@PathVariable Integer examId) {
        ExamStatisticsDTO statistics = resultService.getExamStatistics(examId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
