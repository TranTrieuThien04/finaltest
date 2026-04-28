package com.planbookai.controller;

import com.planbookai.dto.ocr.AnswerKeyRequest;
import com.planbookai.dto.ocr.AnswerKeyResponse;
import com.planbookai.entity.AnswerKey;
import com.planbookai.entity.Exam;
import com.planbookai.repository.AnswerKeyRepository;
import com.planbookai.repository.ExamRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.repository.UserRepository;
import com.planbookai.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/answer-keys")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public class AnswerKeyController {

    private final AnswerKeyRepository answerKeyRepository;
    private final ExamRepository examRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    /** Lấy tất cả đáp án chuẩn */
    @GetMapping
    public List<AnswerKeyResponse> listAll() {
        return answerKeyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Lấy đáp án chuẩn theo examId */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<AnswerKeyResponse> getByExam(@PathVariable @NonNull Long examId) {
        return answerKeyRepository.findByExam_ExamId(examId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Lấy đáp án chuẩn theo examCode */
    @GetMapping("/code/{examCode}")
    public ResponseEntity<AnswerKeyResponse> getByCode(@PathVariable String examCode) {
        return answerKeyRepository.findByExamCode(examCode.trim().toUpperCase())
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo hoặc cập nhật đáp án chuẩn cho một đề thi.
     * Body: { examId, examCode, answersJson }
     * answersJson format: {"part_1":["A","B","C","D",...]}
     */
    @PostMapping
    public ResponseEntity<?> createOrUpdate(@Valid @RequestBody AnswerKeyRequest request) {
        try {
            Long examId = request.getExamId();
            String examCode = request.getExamCode().trim().toUpperCase();
            String answersJson = request.getAnswersJson();

            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));

            Long currentUserId = currentUserService.requireUserId();
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Kiểm tra đã tồn tại chưa
            AnswerKey existing = answerKeyRepository.findByExamCode(examCode).orElse(null);
            if (existing != null) {
                existing.setAnswersJson(answersJson);
                answerKeyRepository.save(existing);
                return ResponseEntity.ok(Map.of(
                    "message", "Đã cập nhật đáp án cho mã đề " + examCode,
                    "answerKey", toResponse(existing)
                ));
            }

            // Kiểm tra examId đã có đáp án chưa
            AnswerKey existingByExam = answerKeyRepository.findByExam_ExamId(examId).orElse(null);
            if (existingByExam != null) {
                existingByExam.setExamCode(examCode);
                existingByExam.setAnswersJson(answersJson);
                answerKeyRepository.save(existingByExam);
                return ResponseEntity.ok(Map.of(
                    "message", "Đã cập nhật đáp án cho đề thi ID " + examId,
                    "answerKey", toResponse(existingByExam)
                ));
            }

            // Tạo mới
            AnswerKey ak = AnswerKey.builder()
                    .exam(exam)
                    .examCode(examCode)
                    .answersJson(answersJson)
                    .createdBy(currentUser)
                    .build();
            AnswerKey saved = answerKeyRepository.save(ak);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Tạo đáp án thành công cho mã đề " + examCode,
                "answerKey", toResponse(saved)
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Xóa đáp án chuẩn */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        answerKeyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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