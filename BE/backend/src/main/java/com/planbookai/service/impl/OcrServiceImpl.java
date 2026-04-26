package com.planbookai.service.impl;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.dto.ocr.OcrSimulateRequest;
import com.planbookai.entity.Exam;
import com.planbookai.entity.OCRResult;
import com.planbookai.repository.ExamRepository;
import com.planbookai.repository.OCRResultRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OCRResultRepository ocrResultRepository;
    private final ExamRepository examRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public OcrResultResponse simulateAndSave(OcrSimulateRequest request) {
        Long examId = Objects.requireNonNull(request.getExamId(), "examId is required");
        Exam exam = getExam(examId);
        ensureExamOwnerOrAdmin(exam);

        double score = Math.round(ThreadLocalRandom.current().nextDouble(5.0, 10.0) * 10.0) / 10.0;
        String resultJson = "{\"status\":\"simulated\",\"confidence\":0.95,\"note\":\"OCR is mocked\"}";

        OCRResult entity = new OCRResult();
        entity.setExam(exam);
        entity.setStudentName(request.getStudentName());
        entity.setScore(score);
        entity.setResultJson(resultJson);
        entity.setGradedAt(LocalDateTime.now());
        return toResponse(ocrResultRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OcrResultResponse> findByExam(@NonNull Long examId) {
        Exam exam = getExam(examId);
        ensureExamOwnerOrAdmin(exam);
        return ocrResultRepository.findByExam_ExamId(examId).stream().map(this::toResponse).toList();
    }

    private @NonNull Exam getExam(@NonNull Long examId) {
        return Objects.requireNonNull(
                examRepository.findById(examId)
                        .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId))
        );
    }

    private void ensureExamOwnerOrAdmin(@NonNull Exam exam) {
        if (currentUserService.hasRole("ADMIN")) {
            return;
        }
        if (!currentUserService.requireUserId().equals(exam.getTeacher().getUserId())) {
            throw new IllegalArgumentException("You can only access OCR results for your own exam");
        }
    }

    private OcrResultResponse toResponse(OCRResult entity) {
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
