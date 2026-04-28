package com.planbookai.service.ocr;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GradingService {

    /**
     * Tính điểm theo chương trình mới (thang 10).
     *
     * @param studentJson  JSON từ Gemini AI, ví dụ: {"student_name":"...", "part_1":["A","B"]}
     * @param keyJson      JSON đáp án chuẩn trong DB, ví dụ: {"part_1":["A","C"]}
     * @return điểm (0.0 - 10.0)
     */
    public double calculateNewCurriculumScore(String studentJson, String keyJson) {
        try {
            JSONObject student = new JSONObject(studentJson);
            JSONObject key = new JSONObject(keyJson);

            int totalCorrect = 0;
            int totalQuestions = 0;

            // Part 1 — Trắc nghiệm (A/B/C/D), mỗi câu bằng nhau
            JSONArray sPart1 = student.optJSONArray("part_1");
            JSONArray kPart1 = key.optJSONArray("part_1");
            if (sPart1 != null && kPart1 != null) {
                int len = Math.min(sPart1.length(), kPart1.length());
                totalQuestions += kPart1.length();
                for (int i = 0; i < len; i++) {
                    String sAns = sPart1.optString(i, "").trim().toUpperCase();
                    String kAns = kPart1.optString(i, "").trim().toUpperCase();
                    if (!sAns.isEmpty() && sAns.equals(kAns)) {
                        totalCorrect++;
                    }
                }
            }

            if (totalQuestions == 0) return 0.0;

            // Tính điểm thang 10, làm tròn 1 chữ số thập phân
            double rawScore = ((double) totalCorrect / totalQuestions) * 10.0;
            return Math.round(rawScore * 10.0) / 10.0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Lấy tên học sinh từ JSON Gemini trả về.
     * Fallback về tên file nếu không có.
     */
    public String extractStudentName(String geminiJson, String fallback) {
        try {
            JSONObject obj = new JSONObject(geminiJson);
            String name = obj.optString("student_name", "").trim();
            return (name.isEmpty() || name.equalsIgnoreCase("Không rõ")) ? fallback : name;
        } catch (Exception e) {
            return fallback;
        }
    }
}