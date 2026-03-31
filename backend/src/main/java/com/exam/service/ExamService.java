package com.exam.service;

import com.exam.dto.CreateExamRequest;
import com.exam.dto.ExamDTO;
import com.exam.entity.Exam;
import com.exam.entity.ExamQuestion;
import com.exam.entity.Question;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.ExamQuestionRepository;
import com.exam.repository.ExamRepository;
import com.exam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UserService userService;
    
    public List<ExamDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ExamDTO> getActiveExams() {
        return examRepository.findByIsActive(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ExamDTO> getMyExams() {
        User currentUser = userService.getCurrentUser();
        return examRepository.findByCreatedBy(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ExamDTO getExamById(Integer id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        return convertToDTO(exam);
    }
    
    public List<ExamDTO> getExamsBySubject(String subject) {
        return examRepository.findBySubject(subject).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ExamDTO createExam(CreateExamRequest request) {
        User currentUser = userService.getCurrentUser();
        
        // Validate questions exist
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.size() != request.getQuestionIds().size()) {
            throw new BadRequestException("Một số câu hỏi không tồn tại");
        }
        
        // Create exam
        Exam exam = new Exam();
        exam.setName(request.getName());
        exam.setDescription(request.getDescription());
        exam.setDuration(request.getDuration());
        exam.setSubject(request.getSubject());
        exam.setPassingScore(request.getPassingScore());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setTotalQuestions(questions.size());
        exam.setIsActive(true);
        exam.setCreatedBy(currentUser);
        
        exam = examRepository.save(exam);
        
        // Add questions to exam
        int order = 1;
        for (Integer questionId : request.getQuestionIds()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow();
            
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExam(exam);
            examQuestion.setQuestion(question);
            examQuestion.setQuestionOrder(order++);
            examQuestionRepository.save(examQuestion);
        }
        
        return convertToDTO(exam);
    }
    
    @Transactional
    public ExamDTO updateExam(Integer id, CreateExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!exam.getCreatedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa đề thi này");
        }
        
        // Update exam info
        exam.setName(request.getName());
        exam.setDescription(request.getDescription());
        exam.setDuration(request.getDuration());
        exam.setSubject(request.getSubject());
        exam.setPassingScore(request.getPassingScore());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        
        // Update questions if provided
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            // Validate questions exist
            List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
            if (questions.size() != request.getQuestionIds().size()) {
                throw new BadRequestException("Một số câu hỏi không tồn tại");
            }
            
            // Remove old questions
            examQuestionRepository.deleteByExam(exam);
            
            // Add new questions
            int order = 1;
            for (Integer questionId : request.getQuestionIds()) {
                Question question = questions.stream()
                        .filter(q -> q.getId().equals(questionId))
                        .findFirst()
                        .orElseThrow();
                
                ExamQuestion examQuestion = new ExamQuestion();
                examQuestion.setExam(exam);
                examQuestion.setQuestion(question);
                examQuestion.setQuestionOrder(order++);
                examQuestionRepository.save(examQuestion);
            }
            
            exam.setTotalQuestions(questions.size());
        }
        
        exam = examRepository.save(exam);
        return convertToDTO(exam);
    }
    
    @Transactional
    public void deleteExam(Integer id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!exam.getCreatedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền xóa đề thi này");
        }
        
        examRepository.deleteById(id);
    }
    
    @Transactional
    public void toggleExamStatus(Integer id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!exam.getCreatedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền thay đổi trạng thái đề thi này");
        }
        
        exam.setIsActive(!exam.getIsActive());
        examRepository.save(exam);
    }
    
    public List<Question> getExamQuestions(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamOrderByQuestionOrder(exam);
        
        return examQuestions.stream()
                .map(ExamQuestion::getQuestion)
                .collect(Collectors.toList());
    }
    
    private ExamDTO convertToDTO(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setId(exam.getId());
        dto.setName(exam.getName());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDuration());
        dto.setSubject(exam.getSubject());
        dto.setTotalQuestions(exam.getTotalQuestions());
        dto.setPassingScore(exam.getPassingScore());
        dto.setStartTime(exam.getStartTime());
        dto.setEndTime(exam.getEndTime());
        dto.setIsActive(exam.getIsActive());
        dto.setCreatedById(exam.getCreatedBy().getId());
        dto.setCreatedByName(exam.getCreatedBy().getFullName());
        dto.setCreatedAt(exam.getCreatedAt());
        return dto;
    }
}
