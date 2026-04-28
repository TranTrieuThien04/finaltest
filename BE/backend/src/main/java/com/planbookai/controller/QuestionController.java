package com.planbookai.controller;

import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Lấy danh sách câu hỏi — có thể filter theo topicId, subjectId, status, difficulty
     * TEACHER, STAFF, MANAGER, ADMIN đều xem được
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','MANAGER','ADMIN')")
    public List<QuestionResponse> list(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) QuestionDifficulty difficulty
    ) {
        return questionService.filter(topicId, subjectId, status, difficulty);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> get(@PathVariable("id") @NonNull Long id) {
        return questionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo câu hỏi vào ngân hàng:
     * - TEACHER: tạo cho bản thân, status = PENDING (cần duyệt)
     * - STAFF: tạo mẫu, status = PENDING (cần Manager duyệt)
     * - ADMIN: tạo và tự duyệt luôn
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','ADMIN')")
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionCreateRequest body) {
        QuestionResponse created = questionService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','ADMIN')")
    public QuestionResponse update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody QuestionUpdateRequest body) {
        return questionService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * MANAGER/ADMIN duyệt câu hỏi từ PENDING → APPROVED
     * PATCH /api/v1/questions/{id}/approve
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> approve(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(questionService.approve(id));
    }

    /**
     * MANAGER/ADMIN từ chối câu hỏi
     * PATCH /api/v1/questions/{id}/reject
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> reject(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(questionService.reject(id));
    }
}