package com.planbookai.controller;

import com.planbookai.dto.approval.ApprovalRequest;
import com.planbookai.dto.approval.ApprovalResponse;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import com.planbookai.service.ApprovalService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * Lấy danh sách approval (phân trang).
     * GET /api/v1/approvals?contentType=QUESTION&status=APPROVED&page=0&size=20
     */
    @GetMapping
    public Page<ApprovalResponse> list(
            @RequestParam(required = false) ApprovalContentType contentType,
            @RequestParam(required = false) ApprovalStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return approvalService.list(contentType, status, pageable);
    }

    /**
     * Chi tiết một approval record.
     * GET /api/v1/approvals/{id}
     */
    @GetMapping("/{id}")
    public ApprovalResponse getById(@PathVariable @NonNull Long id) {
        return approvalService.findById(id);
    }

    /**
     * Tạo approval record (approve hoặc reject) — body đầy đủ.
     * POST /api/v1/approvals
     */
    @PostMapping
    public ResponseEntity<ApprovalResponse> create(@Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(approvalService.create(request));
    }

    /**
     * Shortcut approve một entity.
     * PATCH /api/v1/approvals/approve?contentId=5&contentType=QUESTION
     */
    @PatchMapping("/approve")
    public ApprovalResponse approve(
            @RequestParam @NonNull Long contentId,
            @RequestParam @NonNull ApprovalContentType contentType,
            @RequestParam(required = false) String comment
    ) {
        return approvalService.approve(contentId, contentType, comment);
    }

    /**
     * Shortcut reject một entity.
     * PATCH /api/v1/approvals/reject?contentId=5&contentType=QUESTION
     */
    @PatchMapping("/reject")
    public ApprovalResponse reject(
            @RequestParam @NonNull Long contentId,
            @RequestParam @NonNull ApprovalContentType contentType,
            @RequestParam(required = false) String comment
    ) {
        return approvalService.reject(contentId, contentType, comment);
    }
}
