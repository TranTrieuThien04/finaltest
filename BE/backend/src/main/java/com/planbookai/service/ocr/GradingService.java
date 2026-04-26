package com.planbookai.service.ocr;

import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

@Service
public class GradingService {
    public double calculateNewCurriculumScore(String studentJson, String keyJson) {
        try {
            JSONObject student = new JSONObject(studentJson);
            JSONObject key = new JSONObject(keyJson);
            double totalScore = 0.0;
            // Ví dụ: Part 1 đúng mỗi câu 0.25đ
            JSONArray sPart1 = student.optJSONArray("part_1");
            JSONArray kPart1 = key.optJSONArray("part_1");
            if (sPart1 != null && kPart1 != null) {
                for (int i = 0; i < Math.min(sPart1.length(), kPart1.length()); i++) {
                    if (sPart1.get(i).equals(kPart1.get(i))) totalScore += 0.25;
                }
            }
            return totalScore;
        } catch (Exception e) { return 0.0; }
    }
}