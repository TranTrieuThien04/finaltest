package com.planbookai.service.impl;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.dto.ocr.OcrSimulateRequest;
import com.planbookai.entity.Exam;
import com.planbookai.entity.OcrResult;
import com.planbookai.repository.ExamRepository;
import com.planbookai.repository.OcrResultRepository;
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

    private final OcrResultRepository ocrResultRepository;
    private final ExamRepository examRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public OcrResultResponse simulateAndSave(OcrSimulateRequest request) {
        Long examId = Objects.requireNonNull(request.getExamId(), "examId is required");
        Exam exam = getExamOrThrow(examId);
        ensureExamOwnerOrAdmin(exam);

        double score = Math.round(ThreadLocalRandom.current().nextDouble(5.0, 10.0) * 10.0) / 10.0;

        OcrResult entity = OcrResult.builder()
                .examId(examId)
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
        return ocrResultRepository.findByExamId(examId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

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
                .examId(entity.getExamId())
                .studentName(entity.getStudentName())
                .score(entity.getScore())
                .resultJson(entity.getResultJson())
                .gradedAt(entity.getGradedAt())
                .build();
    }
}