package com.exam.service;

import com.exam.dto.AIQuestionRequest;
import com.exam.dto.CreateQuestionRequest;
import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private WordImportService wordImportService;
    
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionDTO> getMyQuestions() {
        User currentUser = userService.getCurrentUser();
        return questionRepository.findByCreatedBy(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public QuestionDTO getQuestionById(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Câu hỏi không tồn tại"));
        return convertToDTO(question);
    }
    
    public List<QuestionDTO> getQuestionsBySubject(String subject) {
        return questionRepository.findBySubject(subject).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionDTO> getQuestionsByLevel(String level) {
        Question.Level levelEnum = Question.Level.valueOf(level);
        return questionRepository.findByLevel(levelEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public QuestionDTO createQuestion(CreateQuestionRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Question question = new Question();
        question.setContent(request.getContent());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(Question.Answer.valueOf(request.getCorrectAnswer()));
        question.setLevel(Question.Level.valueOf(request.getLevel()));
        question.setSubject(request.getSubject());
        question.setExplanation(request.getExplanation());
        question.setCreatedBy(currentUser);
        
        question = questionRepository.save(question);
        return convertToDTO(question);
    }
    
    @Transactional
    public QuestionDTO updateQuestion(Integer id, CreateQuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Câu hỏi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!question.getCreatedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa câu hỏi này");
        }
        
        question.setContent(request.getContent());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(Question.Answer.valueOf(request.getCorrectAnswer()));
        question.setLevel(Question.Level.valueOf(request.getLevel()));
        question.setSubject(request.getSubject());
        question.setExplanation(request.getExplanation());
        
        question = questionRepository.save(question);
        return convertToDTO(question);
    }
    
    @Transactional
    public void deleteQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Câu hỏi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!question.getCreatedBy().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền xóa câu hỏi này");
        }
        
        questionRepository.deleteById(id);
    }
    
    public List<QuestionDTO> generateQuestionsWithAI(AIQuestionRequest request) {
        // Call AI API to generate questions (NOT saving to database)
        List<CreateQuestionRequest> generatedQuestions = openAIService.generateQuestions(request);
        
        // Convert to DTO for preview (without saving)
        return generatedQuestions.stream()
                .map(req -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setContent(req.getContent());
                    dto.setOptionA(req.getOptionA());
                    dto.setOptionB(req.getOptionB());
                    dto.setOptionC(req.getOptionC());
                    dto.setOptionD(req.getOptionD());
                    dto.setCorrectAnswer(req.getCorrectAnswer());
                    dto.setLevel(req.getLevel());
                    dto.setSubject(request.getSubject());
                    dto.setExplanation(req.getExplanation());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public List<QuestionDTO> importQuestionsFromWord(MultipartFile file) {
        User currentUser = userService.getCurrentUser();
        
        // Parse Word file
        List<CreateQuestionRequest> parsedQuestions = wordImportService.parseWordFile(file);
        
        // Save questions
        List<Question> questions = parsedQuestions.stream()
                .map(req -> {
                    Question question = new Question();
                    question.setContent(req.getContent());
                    question.setOptionA(req.getOptionA());
                    question.setOptionB(req.getOptionB());
                    question.setOptionC(req.getOptionC());
                    question.setOptionD(req.getOptionD());
                    question.setCorrectAnswer(Question.Answer.valueOf(req.getCorrectAnswer()));
                    question.setLevel(Question.Level.valueOf(req.getLevel()));
                    question.setSubject(req.getSubject());
                    question.setExplanation(req.getExplanation());
                    question.setCreatedBy(currentUser);
                    return question;
                })
                .collect(Collectors.toList());
        
        questions = questionRepository.saveAll(questions);
        
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectAnswer(question.getCorrectAnswer().name());
        dto.setLevel(question.getLevel().name());
        dto.setSubject(question.getSubject());
        dto.setExplanation(question.getExplanation());
        dto.setCreatedById(question.getCreatedBy().getId());
        dto.setCreatedByName(question.getCreatedBy().getFullName());
        dto.setCreatedAt(question.getCreatedAt());
        return dto;
    }
}
