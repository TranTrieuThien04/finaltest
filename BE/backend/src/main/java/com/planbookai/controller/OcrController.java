package com.planbookai.controller;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    /**
     * Upload batch ảnh → trả về raw OCR text (dùng để preview).
     * KHÔNG lưu DB, KHÔNG tính điểm.
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> uploadBatch(
            @RequestParam("files") MultipartFile[] files) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                results.add(Map.of(
                    "file", originalName != null ? originalName : "unknown",
                    "error", "Chỉ hỗ trợ file ảnh",
                    "status", "error"
                ));
                continue;
            }
            try {
                String text = ocrService.processImage(file);
                results.add(Map.of(
                    "file", originalName,
                    "text", text,
                    "status", "success"
                ));
            } catch (Exception e) {
                results.add(Map.of(
                    "file", originalName != null ? originalName : "unknown",
                    "error", "Không thể xử lý: " + e.getMessage(),
                    "status", "error"
                ));
            }
        }
        return ResponseEntity.ok(results);
    }

    /**
     * FIX: Upload ảnh + examId + studentName
     *   → Gemini OCR trích xuất đáp án học sinh
     *   → So với AnswerKey trong DB
     *   → Tính điểm thực
     *   → Lưu OcrResult vào DB
     *   → Trả về kết quả đầy đủ
     */
    @PostMapping("/grade")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<?> gradeExam(
            @RequestParam("file") MultipartFile file,
            @RequestParam("examId") Long examId,
            @RequestParam(value = "studentName", defaultValue = "Không rõ") String studentName) {
        try {
            OcrResultResponse result = ocrService.gradeAndSave(file, examId, studentName);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Lỗi chấm bài: " + e.getMessage()));
        }
    }

    /** Lấy danh sách kết quả OCR theo đề thi */
    @GetMapping("/results/{examId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<OcrResultResponse>> getResultsByExam(
            @PathVariable Long examId) {
        return ResponseEntity.ok(ocrService.findByExam(examId));
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        return ResponseEntity.ok(List.of("vie", "eng"));
    }
}