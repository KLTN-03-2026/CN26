package com.exam.service;

import com.exam.dto.CreateQuestionRequest;
import com.exam.exception.BadRequestException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class WordImportService {

    public List<CreateQuestionRequest> parseWordFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".docx")) {
            throw new BadRequestException("File phải có định dạng .docx");
        }

        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {

            List<CreateQuestionRequest> questions = new ArrayList<>();
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            System.out.println("=== Bắt đầu parse file Word ===");
            System.out.println("Tổng số paragraphs: " + paragraphs.size());

            CreateQuestionRequest currentQuestion = null;

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                String paraText = paragraph.getText().trim();

                System.out.println("Paragraph " + i + ": [" + paraText + "]");

                if (paraText.isEmpty()) {
                    continue;
                }

                // Tách từng dòng bên trong paragraph
                String[] lines = paraText.split("\\r?\\n");

                for (String rawLine : lines) {
                    String text = rawLine.trim();

                    if (text.isEmpty()) {
                        continue;
                    }

                    System.out.println("   ↳ Line: [" + text + "]");

                    // Detect question (starts with "Câu X:")
                    if (text.matches("^Câu\\s*\\d+\\s*:.*")) {
                        // Save previous question if exists and complete
                        if (currentQuestion != null) {
                            if (isQuestionComplete(currentQuestion)) {
                                questions.add(currentQuestion);
                                System.out.println("✓ Đã thêm câu hỏi: " + currentQuestion.getContent());
                            } else {
                                System.out.println("✗ Câu hỏi không đầy đủ, bỏ qua");
                                debugQuestion(currentQuestion);
                            }
                        }

                        // Start new question
                        currentQuestion = new CreateQuestionRequest();
                        String content = text.replaceFirst("^Câu\\s*\\d+\\s*:\\s*", "").trim();
                        currentQuestion.setContent(content);
                        currentQuestion.setLevel("medium");
                        System.out.println("→ Bắt đầu câu hỏi mới: " + content);
                    }

                    // Detect option A
                    else if (text.matches("^A\\.\\s+.*")) {
                        if (currentQuestion != null) {
                            String option = text.replaceFirst("^A\\.\\s+", "").trim();
                            currentQuestion.setOptionA(option);
                            System.out.println("  A: " + option);
                        }
                    }

                    // Detect option B
                    else if (text.matches("^B\\.\\s+.*")) {
                        if (currentQuestion != null) {
                            String option = text.replaceFirst("^B\\.\\s+", "").trim();
                            currentQuestion.setOptionB(option);
                            System.out.println("  B: " + option);
                        }
                    }

                    // Detect option C
                    else if (text.matches("^C\\.\\s+.*")) {
                        if (currentQuestion != null) {
                            String option = text.replaceFirst("^C\\.\\s+", "").trim();
                            currentQuestion.setOptionC(option);
                            System.out.println("  C: " + option);
                        }
                    }

                    // Detect option D
                    else if (text.matches("^D\\.\\s+.*")) {
                        if (currentQuestion != null) {
                            String option = text.replaceFirst("^D\\.\\s+", "").trim();
                            currentQuestion.setOptionD(option);
                            System.out.println("  D: " + option);
                        }
                    }

                    // Detect correct answer
                    else if (text.matches("^(Đáp án|Dap an|DAP AN|ĐÁP ÁN)\\s*:\\s*[ABCD]\\s*$")) {
                        if (currentQuestion != null) {
                            String answer = text.replaceFirst("^(Đáp án|Dap an|DAP AN|ĐÁP ÁN)\\s*:\\s*", "").trim();
                            currentQuestion.setCorrectAnswer(answer);
                            System.out.println("  Đáp án: " + answer);
                        }
                    }

                    // Detect explanation (optional)
                    else if (text.startsWith("Giải thích:")) {
                        if (currentQuestion != null) {
                            currentQuestion.setExplanation(text.replaceFirst("^Giải thích:\\s*", "").trim());
                        }
                    }

                    // Detect level (optional)
                    else if (text.matches("^Độ khó:.*")) {
                        if (currentQuestion != null) {
                            String level = extractLevel(text);
                            currentQuestion.setLevel(level);
                        }
                    }
                }
            }

            // Add last question
            if (currentQuestion != null) {
                if (isQuestionComplete(currentQuestion)) {
                    questions.add(currentQuestion);
                    System.out.println("✓ Đã thêm câu hỏi cuối: " + currentQuestion.getContent());
                } else {
                    System.out.println("✗ Câu hỏi cuối không đầy đủ, bỏ qua");
                    debugQuestion(currentQuestion);
                }
            }

            System.out.println("=== Kết thúc parse ===");
            System.out.println("Tổng số câu hỏi hợp lệ: " + questions.size());

            if (questions.isEmpty()) {
                throw new BadRequestException("Không tìm thấy câu hỏi hợp lệ trong file. Vui lòng kiểm tra định dạng.");
            }

            return questions;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Không thể đọc file Word: " + e.getMessage());
        }
    }

    private boolean isQuestionComplete(CreateQuestionRequest question) {
        return question.getContent() != null && !question.getContent().isEmpty() &&
               question.getOptionA() != null && !question.getOptionA().isEmpty() &&
               question.getOptionB() != null && !question.getOptionB().isEmpty() &&
               question.getOptionC() != null && !question.getOptionC().isEmpty() &&
               question.getOptionD() != null && !question.getOptionD().isEmpty() &&
               question.getCorrectAnswer() != null && !question.getCorrectAnswer().isEmpty();
    }

    private String extractLevel(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.contains("dễ") || lowerText.contains("easy")) return "easy";
        if (lowerText.contains("khó") || lowerText.contains("hard")) return "hard";
        return "medium";
    }

    private void debugQuestion(CreateQuestionRequest q) {
        System.out.println("  Content: " + q.getContent());
        System.out.println("  OptionA: " + q.getOptionA());
        System.out.println("  OptionB: " + q.getOptionB());
        System.out.println("  OptionC: " + q.getOptionC());
        System.out.println("  OptionD: " + q.getOptionD());
        System.out.println("  Answer: " + q.getCorrectAnswer());
    }
}