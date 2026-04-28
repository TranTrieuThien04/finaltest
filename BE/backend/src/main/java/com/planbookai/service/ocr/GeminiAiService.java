package com.planbookai.service.ocr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Gửi ảnh bài thi lên Gemini Vision để trích xuất đáp án học sinh.
     * Trả về JSON string, ví dụ:
     * {"student_name":"Nguyen Van A","part_1":["A","B","C","D"]}
     */
    public String analyzeImageWithGemini(Path imagePath) throws IOException {
        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));

        // Detect mime type từ extension
        String fileName = imagePath.getFileName().toString().toLowerCase();
        String mimeType = "image/jpeg";
        if (fileName.endsWith(".png")) mimeType = "image/png";
        else if (fileName.endsWith(".webp")) mimeType = "image/webp";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = """
                Bạn là hệ thống OCR chấm bài thi trắc nghiệm Hóa học.
                Phân tích ảnh bài làm của học sinh và trích xuất thông tin.
                Trả về JSON CHÍNH XÁC theo định dạng sau (không thêm text khác):
                {
                  "student_name": "tên học sinh (nếu có, nếu không để 'Không rõ')",
                  "part_1": ["A", "B", "C", "D"]
                }
                Trong đó part_1 là mảng đáp án theo thứ tự câu hỏi (chỉ A/B/C/D).
                Nếu không đọc được một câu nào, để null cho câu đó.
                """;

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(
                Map.of("text", prompt),
                Map.of("inline_data", Map.of("mime_type", mimeType, "data", base64))
            ))),
            "generationConfig", Map.of(
                "response_mime_type", "application/json",
                "temperature", 0.1,
                "maxOutputTokens", 512
            )
        );

        return callGeminiApi(url, body);
    }

    @SuppressWarnings("unchecked")
    private String callGeminiApi(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);

            if (res.getBody() == null) {
                return "{\"error\":\"Empty response from Gemini\"}";
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "{\"error\":\"No candidates in Gemini response\"}";
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // Cleanup: đôi khi Gemini wrap thêm ```json ... ```
            if (text != null) {
                text = text.trim()
                    .replaceAll("^```json\\s*", "")
                    .replaceAll("^```\\s*", "")
                    .replaceAll("\\s*```$", "")
                    .trim();
            }

            return text != null ? text : "{}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}";
        }
    }
}