package com.exam.service;

import com.exam.dto.AIQuestionRequest;
import com.exam.dto.CreateQuestionRequest;
import com.exam.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${google.ai.api.key}")
    private String apiKey;

    @Value("${google.ai.model:gemini-2.5-flash}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAIService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<CreateQuestionRequest> generateQuestions(AIQuestionRequest request) {
        try {
            String prompt = buildPrompt(request);
            String response = callGoogleAI(prompt);
            return parseResponse(response, request);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Không thể sinh câu hỏi: " + e.getMessage());
        }
    }

    private String buildPrompt(AIQuestionRequest request) {
        return String.format("""
            Hãy tạo chính xác %d câu hỏi trắc nghiệm về chủ đề "%s" với độ khó "%s".

            Yêu cầu bắt buộc:
            - Mỗi câu có đúng 4 đáp án: A, B, C, D
            - Chỉ có 1 đáp án đúng
            - Nội dung rõ ràng, phù hợp học sinh/sinh viên
            - Trả về DUY NHẤT JSON array hợp lệ
            - KHÔNG thêm markdown
            - KHÔNG thêm giải thích ngoài JSON
            - KHÔNG thêm text trước hoặc sau JSON

            Format JSON bắt buộc:
            [
              {
                "content": "Nội dung câu hỏi",
                "optionA": "Đáp án A",
                "optionB": "Đáp án B",
                "optionC": "Đáp án C",
                "optionD": "Đáp án D",
                "correctAnswer": "A",
                "explanation": "Giải thích ngắn gọn"
              }
            ]
            """,
            request.getQuantity(),
            request.getTopic(),
            request.getLevel()
        );
    }

    private String callGoogleAI(String prompt) {
        String endpoint = String.format(
                "/v1beta/models/%s:generateContent?key=%s",
                model,
                apiKey
        );

        Map<String, Object> requestBody = new HashMap<>();

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();

        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);

        content.put("parts", parts);
        contents.add(content);

        requestBody.put("contents", contents);

        System.out.println("=== CALL GOOGLE AI ===");
        System.out.println("Model: " + model);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Prompt: " + prompt);

        try {
            String response = webClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("=== GOOGLE AI RESPONSE ===");
            System.out.println(response);

            return response;

        } catch (WebClientResponseException e) {
            System.out.println("=== GOOGLE AI ERROR ===");
            System.out.println("Status Code: " + e.getStatusCode());
            System.out.println("Response Body: " + e.getResponseBodyAsString());

            throw new BadRequestException(
                    "Google AI lỗi: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
            );
        }
    }

    private List<CreateQuestionRequest> parseResponse(String response, AIQuestionRequest request) {
        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                throw new BadRequestException("Google AI không trả về candidates hợp lệ");
            }

            JsonNode textNode = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            if (textNode == null || textNode.isMissingNode()) {
                throw new BadRequestException("Google AI không trả về nội dung text");
            }

            String content = textNode.asText();

            System.out.println("=== RAW AI CONTENT ===");
            System.out.println(content);

            // Remove markdown code blocks if present
            content = content.replaceAll("```json\\s*", "")
                             .replaceAll("```\\s*", "")
                             .trim();

            JsonNode questionsArray = objectMapper.readTree(content);

            if (!questionsArray.isArray()) {
                throw new BadRequestException("AI không trả về JSON array hợp lệ");
            }

            List<CreateQuestionRequest> questions = new ArrayList<>();

            for (JsonNode questionNode : questionsArray) {
                CreateQuestionRequest question = new CreateQuestionRequest();
                question.setContent(questionNode.path("content").asText());
                question.setOptionA(questionNode.path("optionA").asText());
                question.setOptionB(questionNode.path("optionB").asText());
                question.setOptionC(questionNode.path("optionC").asText());
                question.setOptionD(questionNode.path("optionD").asText());
                question.setCorrectAnswer(questionNode.path("correctAnswer").asText());
                question.setLevel(request.getLevel());
                question.setSubject(request.getSubject());
                question.setExplanation(questionNode.path("explanation").asText());

                questions.add(question);
            }

            if (questions.isEmpty()) {
                throw new BadRequestException("AI không sinh được câu hỏi hợp lệ");
            }

            return questions;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Không thể parse response từ Google AI: " + e.getMessage());
        }
    }
}