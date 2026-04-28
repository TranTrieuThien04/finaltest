package com.planbookai.controller;

import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Lấy danh sách câu hỏi có phân trang + filter.
     * GET /api/v1/questions?topicId=1&status=APPROVED&page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','MANAGER','ADMIN')")
    public Page<QuestionResponse> list(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return questionService.filterPaged(topicId, subjectId, status, difficulty, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> get(@PathVariable("id") @NonNull Long id) {
        return questionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo câu hỏi:
     * - TEACHER/STAFF → status = PENDING (cần duyệt)
     * - ADMIN → status = APPROVED tự động
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','ADMIN')")
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionCreateRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(body));
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

    /** MANAGER/ADMIN duyệt câu hỏi PENDING → APPROVED + ghi audit */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> approve(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(questionService.approve(id));
    }

    /** MANAGER/ADMIN từ chối câu hỏi + ghi audit */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<QuestionResponse> reject(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(questionService.reject(id));
    }
}
