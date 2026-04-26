package com.planbookai.controller;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.dto.ocr.OcrSimulateRequest;
import com.planbookai.entity.AnswerKey;
import com.planbookai.entity.OcrResult;
import com.planbookai.repository.AnswerKeyRepository;
import com.planbookai.repository.OcrResultRepository;
import com.planbookai.service.OcrService;
import com.planbookai.service.ocr.GeminiAiService;
import com.planbookai.service.ocr.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public class OcrController {

    private final OcrService ocrService;
    private final GeminiAiService geminiAiService;
    private final GradingService gradingService;
    
    private final OcrResultRepository ocrResultRepository;
    // Inject thêm Repo để kéo đáp án từ Database
    private final AnswerKeyRepository answerKeyRepository; 

    // ==========================================
    // 1. API CŨ (Giữ nguyên)
    // ==========================================
    @PostMapping("/simulate")
    public ResponseEntity<OcrResultResponse> simulate(@Valid @RequestBody OcrSimulateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ocrService.simulateAndSave(request));
    }

    @GetMapping("/exam/{examId}")
    public List<OcrResultResponse> byExam(@PathVariable @NonNull Long examId) {
        return ocrService.findByExam(examId);
    }

    // ==========================================
    // 2. API MỚI (CHẤM ĐIỂM AI VỚI ĐÁP ÁN THẬT)
    // ==========================================
    @PostMapping("/upload-batch")
    public ResponseEntity<?> uploadBatch(
            @RequestParam("files") MultipartFile[] files, 
            @RequestParam("examCode") String examCode) {
        
        List<OcrResult> batchResults = new ArrayList<>();
        
        try {
            // 1. Kéo đáp án chuẩn từ Database thay vì hardcode
            AnswerKey answerKey = answerKeyRepository.findByExamCode(examCode);
            if (answerKey == null) {
                return ResponseEntity.badRequest().body("Lỗi: Chưa có đáp án chuẩn cho mã đề " + examCode);
            }
            String answerKeyJson = answerKey.getAnswersJson();

            for (MultipartFile file : files) {
                File tempFile = Files.createTempFile("pba_batch_", file.getOriginalFilename()).toFile();
                file.transferTo(tempFile);
                
                // 2. Gọi AI quét ảnh bài làm
                String rawJson = geminiAiService.analyzeImageWithGemini(tempFile.toPath());
                
                // 3. Tính điểm bằng cách so sánh với answerKeyJson từ DB
                double score = gradingService.calculateNewCurriculumScore(rawJson, answerKeyJson);
                
                // 4. Đóng gói kết quả
                OcrResult res = new OcrResult();
                res.setStudentName(new JSONObject(rawJson).optString("student_name", "HS: " + file.getOriginalFilename()));
                res.setScore(score);
                res.setResultJson(rawJson);
                res.setRequiresReview(false);
                res.setGradedAt(LocalDateTime.now());
                res.setExamId(Long.parseLong(examCode)); 
                
                // 5. Lưu vào DB
                batchResults.add(ocrResultRepository.save(res));
                tempFile.delete();
            }
            return ResponseEntity.ok(batchResults);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi chấm bài AI: " + e.getMessage());
        }
    }
}