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
    @Value("${gemini.api.key}") private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeImageWithGemini(Path imagePath) throws IOException {
        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "Bạn là máy quét OCR bài thi Hóa học chuyên nghiệp. Trích xuất dữ liệu sang JSON: " +
                "{ \"student_name\": \"...\", \"part_1\": [\"A\", \"B\"], \"part_2\": [\"Đ,S,S,Đ\"], \"part_3\": [\"12.5\"] }";

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(
                Map.of("text", prompt),
                Map.of("inline_data", Map.of("mime_type", "image/jpeg", "data", base64))
            ))),
            "generationConfig", Map.of("response_mime_type", "application/json", "temperature", 0.1)
        );
        return callApi(url, body);
    }

    private String callApi(String url, Map<String, Object> body) {
        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(url, new HttpEntity<>(body), Map.class);
            List<Map> candidates = (List<Map>) res.getBody().get("candidates");
            List<Map> parts = (List<Map>) ((Map) candidates.get(0).get("content")).get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) { e.printStackTrace(); return "{}"; }
    }
}s