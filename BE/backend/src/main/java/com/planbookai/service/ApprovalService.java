package com.planbookai.service;

import com.planbookai.dto.approval.ApprovalRequest;
import com.planbookai.dto.approval.ApprovalResponse;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface ApprovalService {

    /**
     * Tạo approval record + cập nhật status entity gốc (Question / LessonPlan / Prompt).
     * Chỉ MANAGER hoặc ADMIN được gọi.
     */
    ApprovalResponse create(ApprovalRequest request);

    /**
     * Lấy danh sách approval có thể filter theo contentType và/hoặc status.
     */
    Page<ApprovalResponse> list(ApprovalContentType contentType,
                                 ApprovalStatus status,
                                 Pageable pageable);

    /**
     * Chi tiết một approval record.
     */
    ApprovalResponse findById(@NonNull Long approvalId);

    /**
     * Shortcut: approve một entity (tạo approval record APPROVED + cập nhật entity).
     */
    ApprovalResponse approve(@NonNull Long contentId,
                              @NonNull ApprovalContentType contentType,
                              String comment);

    /**
     * Shortcut: reject một entity (tạo approval record REJECTED + cập nhật entity).
     */
    ApprovalResponse reject(@NonNull Long contentId,
                             @NonNull ApprovalContentType contentType,
                             String comment);
}
