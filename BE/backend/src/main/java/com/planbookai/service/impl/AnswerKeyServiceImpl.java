package com.planbookai.service.impl;

import com.planbookai.dto.ocr.AnswerKeyRequest;
import com.planbookai.dto.ocr.AnswerKeyResponse;
import com.planbookai.entity.AnswerKey;
import com.planbookai.entity.Exam;
import com.planbookai.entity.User;
import com.planbookai.repository.AnswerKeyRepository;
import com.planbookai.repository.ExamRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.AnswerKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerKeyServiceImpl implements AnswerKeyService {

    private final AnswerKeyRepository answerKeyRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<AnswerKeyResponse> findAll() {
        // FIX: ADMIN xem tất cả; TEACHER chỉ xem đáp án của đề mình tạo
        if (currentUserService.hasRole("ADMIN") || currentUserService.hasRole("MANAGER")) {
            return answerKeyRepository.findAll().stream().map(this::toResponse).toList();
        }
        Long userId = currentUserService.requireUserId();
        return answerKeyRepository.findByExam_Teacher_UserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerKeyResponse findByExamId(@NonNull Long examId) {
        AnswerKey ak = answerKeyRepository.findByExam_ExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Answer key not found for exam: " + examId));
        ensureOwnerOrAdmin(ak);
        return toResponse(ak);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerKeyResponse findByExamCode(String examCode) {
        AnswerKey ak = answerKeyRepository.findByExamCode(examCode.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Answer key not found for code: " + examCode));
        ensureOwnerOrAdmin(ak);
        return toResponse(ak);
    }

    @Override
    @Transactional
    public AnswerKeyResponse createOrUpdate(AnswerKeyRequest request) {
        Long examId = request.getExamId();
        String examCode = request.getExamCode().trim().toUpperCase();
        String answersJson = request.getAnswersJson();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));

        // Chỉ owner hoặc ADMIN mới được tạo/cập nhật đáp án cho đề
        if (!currentUserService.hasRole("ADMIN")) {
            Long userId = currentUserService.requireUserId();
            if (!userId.equals(exam.getTeacher().getUserId())) {
                throw new IllegalArgumentException("Bạn không có quyền tạo đáp án cho đề thi này");
            }
        }

        Long currentUserId = currentUserService.requireUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

        // Upsert by examCode
        AnswerKey existing = answerKeyRepository.findByExamCode(examCode).orElse(null);
        if (existing != null) {
            existing.setAnswersJson(answersJson);
            return toResponse(answerKeyRepository.save(existing));
        }

        // Upsert by examId
        AnswerKey existingByExam = answerKeyRepository.findByExam_ExamId(examId).orElse(null);
        if (existingByExam != null) {
            existingByExam.setExamCode(examCode);
            existingByExam.setAnswersJson(answersJson);
            return toResponse(answerKeyRepository.save(existingByExam));
        }

        // Tạo mới
        AnswerKey ak = AnswerKey.builder()
                .exam(exam)
                .examCode(examCode)
                .answersJson(answersJson)
                .createdBy(currentUser)
                .build();
        return toResponse(answerKeyRepository.save(ak));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long id) {
        AnswerKey ak = answerKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Answer key not found: " + id));
        ensureOwnerOrAdmin(ak);
        answerKeyRepository.deleteById(id);
    }

    // ==================== PRIVATE ====================

    private void ensureOwnerOrAdmin(AnswerKey ak) {
        if (currentUserService.hasRole("ADMIN") || currentUserService.hasRole("MANAGER")) return;
        Long userId = currentUserService.requireUserId();
        if (ak.getExam().getTeacher() != null
                && !userId.equals(ak.getExam().getTeacher().getUserId())) {
            throw new IllegalArgumentException("Bạn không có quyền xem đáp án này");
        }
    }

    private AnswerKeyResponse toResponse(AnswerKey ak) {
        return AnswerKeyResponse.builder()
                .answerKeyId(ak.getAnswerKeyId())
                .examId(ak.getExam().getExamId())
                .examCode(ak.getExamCode())
                .answersJson(ak.getAnswersJson())
                .createdAt(ak.getCreatedAt())
                .build();
    }
}
