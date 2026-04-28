package com.planbookai.service.impl;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.dto.ocr.OcrSimulateRequest;
import com.planbookai.entity.Exam;
import com.planbookai.entity.OcrResult;
import com.planbookai.repository.ExamRepository;
import com.planbookai.repository.OcrResultRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.OcrService;
import com.planbookai.service.ocr.GeminiAiService;
import com.planbookai.service.ocr.GradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrResultRepository ocrResultRepository;
    private final ExamRepository examRepository;
    private final CurrentUserService currentUserService;
    private final GeminiAiService geminiAiService;
    private final GradingService gradingService;

    @Override
    @Transactional
    public OcrResultResponse simulateAndSave(OcrSimulateRequest request) {
        Long examId = Objects.requireNonNull(request.getExamId(), "examId is required");
        Exam exam = getExamOrThrow(examId);
        ensureExamOwnerOrAdmin(exam);

        // Dùng GradingService để tính điểm demo (không cần ảnh thật)
        double score = Math.round((5.0 + Math.random() * 5.0) * 10.0) / 10.0;

        OcrResult entity = OcrResult.builder()
                .exam(exam)
                .studentName(request.getStudentName())
                .score(score)
                .resultJson("{\"status\":\"simulated\",\"note\":\"OCR is mocked for demo\"}")
                .requiresReview(false)
                .gradedAt(LocalDateTime.now())
                .build();

        return toResponse(ocrResultRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OcrResultResponse> findByExam(@NonNull Long examId) {
        Exam exam = getExamOrThrow(examId);
        ensureExamOwnerOrAdmin(exam);
        return ocrResultRepository.findByExam_ExamId(examId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * OCR thực tế: upload ảnh bài làm → Gemini Vision trích xuất JSON đáp án học sinh.
     * Trả về JSON string dạng: {"student_name":"...", "part_1":["A","B","C",...]}
     */
    @Override
    public String processImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được rỗng");
        }

        String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "upload.jpg";
        String suffix = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";

        // Ghi file tạm để truyền vào GeminiAiService
        Path tempFile = Files.createTempFile("ocr_", suffix);
        try {
            file.transferTo(tempFile);
            log.info("OCR: xử lý file {} ({} bytes)", originalFilename, file.getSize());
            String geminiJson = geminiAiService.analyzeImageWithGemini(tempFile);
            log.info("OCR: Gemini trả về: {}", geminiJson);
            return geminiJson;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // ==================== PRIVATE ====================

    private Exam getExamOrThrow(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));
    }

    private void ensureExamOwnerOrAdmin(Exam exam) {
        if (currentUserService.hasRole("ADMIN")) return;
        if (!currentUserService.requireUserId().equals(exam.getTeacher().getUserId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể xem kết quả OCR của đề thi của mình");
        }
    }

    private OcrResultResponse toResponse(OcrResult entity) {
        return OcrResultResponse.builder()
                .ocrResultId(entity.getOcrResultId())
                .examId(entity.getExam().getExamId())
                .studentName(entity.getStudentName())
                .score(entity.getScore())
                .resultJson(entity.getResultJson())
                .gradedAt(entity.getGradedAt())
                .build();
    }
}
