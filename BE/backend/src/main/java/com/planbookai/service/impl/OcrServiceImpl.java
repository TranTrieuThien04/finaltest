package com.planbookai.service.impl;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.entity.AnswerKey;
import com.planbookai.entity.Exam;
import com.planbookai.entity.OcrResult;
import com.planbookai.repository.AnswerKeyRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrResultRepository ocrResultRepository;
    private final ExamRepository examRepository;
    private final AnswerKeyRepository answerKeyRepository;   // FIX: thêm
    private final CurrentUserService currentUserService;
    private final GeminiAiService geminiAiService;
    private final GradingService gradingService;             // FIX: nay được dùng thật

    /**
     * FIX: Full flow hoàn chỉnh
     * 1. Gọi Gemini Vision → trích xuất đáp án học sinh (JSON)
     * 2. Lấy AnswerKey từ DB theo examId
     * 3. GradingService tính điểm thực (so khớp từng câu)
     * 4. Lưu OcrResult vào DB
     * 5. Trả về kết quả đầy đủ
     */
    @Override
    @Transactional
    public OcrResultResponse gradeAndSave(MultipartFile file, Long examId, String studentName)
            throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được rỗng");
        }

        // 1. Lấy Exam
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đề thi ID: " + examId));
        ensureExamOwnerOrAdmin(exam);

        // 2. Lấy AnswerKey — bắt buộc phải có trước khi chấm
        AnswerKey answerKey = answerKeyRepository.findByExam_ExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Đề thi ID=" + examId + " chưa có đáp án chuẩn. " +
                    "Vui lòng nhập đáp án trước khi chấm bài."));

        // 3. Gọi Gemini OCR
        String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "upload.jpg";
        String suffix = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";

        Path tempFile = Files.createTempFile("ocr_grade_", suffix);
        String geminiJson;
        try {
            file.transferTo(tempFile);
            log.info("OCR grade: xử lý file {} cho examId={}", originalFilename, examId);
            geminiJson = geminiAiService.analyzeImageWithGemini(tempFile);
            log.info("OCR grade: Gemini trả về: {}", geminiJson);
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // 4. Tính điểm thực bằng GradingService (FIX: trước đây không được gọi)
        String keyJson = answerKey.getAnswersJson();
        double score = gradingService.calculateNewCurriculumScore(geminiJson, keyJson);

        // 5. Trích xuất tên học sinh từ JSON Gemini (nếu có)
        String resolvedName = gradingService.extractStudentName(geminiJson, studentName);

        // 6. Xác định có cần review không (điểm 0 hoặc JSON lỗi)
        boolean requiresReview = geminiJson.contains("\"error\"") || score == 0.0;

        // 7. Lưu vào DB
        OcrResult entity = OcrResult.builder()
                .exam(exam)
                .studentName(resolvedName)
                .score(score)
                .resultJson(geminiJson)
                .requiresReview(requiresReview)
                .gradedAt(LocalDateTime.now())
                .build();

        return toResponse(ocrResultRepository.save(entity));
    }

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
        Path tempFile = Files.createTempFile("ocr_", suffix);
        try {
            file.transferTo(tempFile);
            return geminiAiService.analyzeImageWithGemini(tempFile);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OcrResultResponse> findByExam(@NonNull Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));
        ensureExamOwnerOrAdmin(exam);
        return ocrResultRepository.findByExam_ExamId(examId)
                .stream().map(this::toResponse).toList();
    }

    private void ensureExamOwnerOrAdmin(Exam exam) {
        if (currentUserService.hasRole("ADMIN")) return;
        if (!currentUserService.requireUserId().equals(exam.getTeacher().getUserId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể xem kết quả của đề thi mình tạo");
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