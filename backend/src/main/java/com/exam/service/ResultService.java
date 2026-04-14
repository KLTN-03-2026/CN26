package com.exam.service;

import com.exam.dto.ExamStatisticsDTO;
import com.exam.dto.ResultDTO;
import com.exam.dto.SubmitAnswerRequest;
import com.exam.entity.*;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;
    
    @Autowired
    private AnswerRepository answerRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    @Autowired
    private UserService userService;
    
    @Transactional
    public ResultDTO startExam(Integer examId) {
        User currentUser = userService.getCurrentUser();
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        // Check if exam is active
        if (!exam.getIsActive()) {
            throw new BadRequestException("Đề thi không khả dụng");
        }
        
        // Check if exam time is valid
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new BadRequestException("Đề thi chưa bắt đầu");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new BadRequestException("Đề thi đã kết thúc");
        }
        
        // Check if user already has an in-progress attempt
        resultRepository.findByUserAndExamAndStatus(currentUser, exam, Result.Status.in_progress)
                .ifPresent(result -> {
                    throw new BadRequestException("Bạn đang có bài thi chưa hoàn thành");
                });
        
        // Create new result
        Result result = new Result();
        result.setUser(currentUser);
        result.setExam(exam);
        result.setScore(BigDecimal.ZERO);
        result.setTotalCorrect(0);
        result.setTotalQuestions(exam.getTotalQuestions());
        result.setStatus(Result.Status.in_progress);
        
        result = resultRepository.save(result);
        return convertToDTO(result);
    }
    
    @Transactional
    public ResultDTO submitExam(SubmitAnswerRequest request) {
        Result result = resultRepository.findById(request.getResultId())
                .orElseThrow(() -> new ResourceNotFoundException("Kết quả thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!result.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền nộp bài thi này");
        }
        
        if (result.getStatus() != Result.Status.in_progress) {
            throw new BadRequestException("Bài thi đã được nộp");
        }
        
        // Save answers
        for (SubmitAnswerRequest.AnswerItem answerItem : request.getAnswers()) {
            Question question = questionRepository.findById(answerItem.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Câu hỏi không tồn tại"));
            
            Answer answer = new Answer();
            answer.setResult(result);
            answer.setQuestion(question);
            answer.setSelectedAnswer(Question.Answer.valueOf(answerItem.getSelectedAnswer()));
            answer.setIsCorrect(question.getCorrectAnswer().name().equals(answerItem.getSelectedAnswer()));
            
            answerRepository.save(answer);
        }
        
        // Calculate score
        long correctCount = answerRepository.countByResultAndIsCorrect(result, true);
        BigDecimal score = BigDecimal.valueOf(correctCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(result.getTotalQuestions()), 2, RoundingMode.HALF_UP);
        
        // Calculate time spent
        Duration duration = Duration.between(result.getStartTime(), LocalDateTime.now());
        int timeSpent = (int) duration.toSeconds();
        
        // Update result
        result.setScore(score);
        result.setTotalCorrect((int) correctCount);
        result.setTimeSpent(timeSpent);
        result.setSubmitTime(LocalDateTime.now());
        result.setStatus(Result.Status.graded);
        
        result = resultRepository.save(result);
        return convertToDTO(result);
    }
    
    public List<ResultDTO> getMyResults() {
        User currentUser = userService.getCurrentUser();
        return resultRepository.findByUser(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ResultDTO getResultById(Integer id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kết quả thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!result.getUser().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.teacher && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền xem kết quả này");
        }
        
        return convertToDTO(result);
    }
    
    public List<Answer> getResultAnswers(Integer resultId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Kết quả thi không tồn tại"));
        
        User currentUser = userService.getCurrentUser();
        if (!result.getUser().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.Role.teacher && 
            currentUser.getRole() != User.Role.admin) {
            throw new BadRequestException("Bạn không có quyền xem chi tiết bài làm");
        }
        
        return answerRepository.findByResult(result);
    }
    
    public List<ResultDTO> getExamResults(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        return resultRepository.findByExamAndStatus(exam, Result.Status.graded).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ExamStatisticsDTO getExamStatistics(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề thi không tồn tại"));
        
        ExamStatisticsDTO stats = new ExamStatisticsDTO();
        stats.setExamId(exam.getId());
        stats.setExamName(exam.getName());
        stats.setExamDescription(exam.getDescription());
        
        long totalAttempts = resultRepository.countByExamAndStatus(exam, Result.Status.graded);
        stats.setTotalAttempts(totalAttempts);
        
        List<Result> results = resultRepository.findByExamAndStatus(exam, Result.Status.graded);
        long totalStudents = results.stream()
                .map(r -> r.getUser().getId())
                .distinct()
                .count();
        stats.setTotalStudents(totalStudents);
        
        BigDecimal avgScore = resultRepository.findAverageScoreByExam(exam);
        stats.setAverageScore(avgScore != null ? avgScore : BigDecimal.ZERO);
        
        BigDecimal maxScore = resultRepository.findMaxScoreByExam(exam);
        stats.setHighestScore(maxScore != null ? maxScore : BigDecimal.ZERO);
        
        BigDecimal minScore = resultRepository.findMinScoreByExam(exam);
        stats.setLowestScore(minScore != null ? minScore : BigDecimal.ZERO);
        
        long passedCount = results.stream()
                .filter(r -> r.getScore().compareTo(exam.getPassingScore()) >= 0)
                .count();
        stats.setPassedCount(passedCount);
        
        BigDecimal passRate = totalAttempts > 0 
                ? BigDecimal.valueOf(passedCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalAttempts), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        stats.setPassRate(passRate);
        
        // Add individual results
        List<ExamStatisticsDTO.StudentResult> studentResults = results.stream()
                .map(r -> {
                    ExamStatisticsDTO.StudentResult sr = new ExamStatisticsDTO.StudentResult();
                    sr.setId(r.getId());
                    sr.setStudentName(r.getUser().getFullName());
                    sr.setStudentEmail(r.getUser().getEmail());
                    sr.setScore(r.getScore());
                    sr.setCorrectAnswers(r.getTotalCorrect());
                    sr.setTotalQuestions(r.getTotalQuestions());
                    sr.setTimeSpent(r.getTimeSpent());
                    sr.setSubmitTime(r.getSubmitTime());
                    return sr;
                })
                .collect(Collectors.toList());
        stats.setResults(studentResults);
        
        // Add question analysis
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(exam);
        List<ExamStatisticsDTO.QuestionAnalysis> questionAnalysis = examQuestions.stream()
                .map(eq -> {
                    Question question = eq.getQuestion();
                    ExamStatisticsDTO.QuestionAnalysis qa = new ExamStatisticsDTO.QuestionAnalysis();
                    qa.setQuestionId(question.getId());
                    qa.setContent(question.getContent());
                    qa.setCorrectAnswer(question.getCorrectAnswer().name());
                    
                    int totalAnswers = 0;
                    int correctCount = 0;
                    for (Result result : results) {
                        List<Answer> answers = answerRepository.findByResultAndQuestion(result, question);
                        if (!answers.isEmpty()) {
                            totalAnswers++;
                            if (answers.get(0).getIsCorrect()) {
                                correctCount++;
                            }
                        }
                    }
                    
                    qa.setTotalAnswers(totalAnswers);
                    qa.setCorrectCount(correctCount);
                    BigDecimal correctPercentage = totalAnswers > 0
                            ? BigDecimal.valueOf(correctCount)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(totalAnswers), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    qa.setCorrectPercentage(correctPercentage);
                    
                    return qa;
                })
                .collect(Collectors.toList());
        stats.setQuestionAnalysis(questionAnalysis);
        
        return stats;
    }
    
    private ResultDTO convertToDTO(Result result) {
        ResultDTO dto = new ResultDTO();
        dto.setId(result.getId());
        dto.setUserId(result.getUser().getId());
        dto.setUserName(result.getUser().getFullName());
        dto.setUserEmail(result.getUser().getEmail());
        dto.setExamId(result.getExam().getId());
        dto.setExamName(result.getExam().getName());
        dto.setScore(result.getScore());
        dto.setTotalCorrect(result.getTotalCorrect());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setTimeSpent(result.getTimeSpent());
        dto.setStatus(result.getStatus().name());
        dto.setStartTime(result.getStartTime());
        dto.setSubmitTime(result.getSubmitTime());
        return dto;
    }
}
