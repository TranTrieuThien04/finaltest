package com.planbookai.service.impl;

import com.planbookai.dto.approval.ApprovalRequest;
import com.planbookai.dto.approval.ApprovalResponse;
import com.planbookai.entity.Approval;
import com.planbookai.entity.LessonPlanTemplate;
import com.planbookai.entity.PromptTemplate;
import com.planbookai.entity.Question;
import com.planbookai.entity.User;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.entity.enums.TemplateStatus;
import com.planbookai.repository.ApprovalRepository;
import com.planbookai.repository.LessonPlanTemplateRepository;
import com.planbookai.repository.PromptTemplateRepository;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final QuestionRepository questionRepository;
    private final LessonPlanTemplateRepository templateRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ApprovalResponse create(ApprovalRequest request) {
        Long approverId = currentUserService.requireUserId();
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + approverId));

        // Cập nhật status entity gốc
        applyStatusToEntity(request.getContentId(), request.getContentType(), request.getStatus());

        Approval approval = new Approval();
        approval.setApprover(approver);
        approval.setContentId(request.getContentId());
        approval.setContentType(request.getContentType());
        approval.setStatus(request.getStatus());
        approval.setComment(request.getComment());
        approval.setCreatedAt(LocalDateTime.now());

        return toResponse(approvalRepository.save(approval));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> list(ApprovalContentType contentType,
                                        ApprovalStatus status,
                                        Pageable pageable) {
        Page<Approval> page;
        if (contentType != null && status != null) {
            page = approvalRepository.findByContentTypeAndStatus(contentType, status, pageable);
        } else if (contentType != null) {
            page = approvalRepository.findByContentType(contentType, pageable);
        } else if (status != null) {
            page = approvalRepository.findByStatus(status, pageable);
        } else {
            page = approvalRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse findById(@NonNull Long approvalId) {
        return approvalRepository.findById(approvalId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found: " + approvalId));
    }

    @Override
    @Transactional
    public ApprovalResponse approve(@NonNull Long contentId,
                                     @NonNull ApprovalContentType contentType,
                                     String comment) {
        ApprovalRequest req = new ApprovalRequest();
        req.setContentId(contentId);
        req.setContentType(contentType);
        req.setStatus(ApprovalStatus.APPROVED);
        req.setComment(comment);
        return create(req);
    }

    @Override
    @Transactional
    public ApprovalResponse reject(@NonNull Long contentId,
                                    @NonNull ApprovalContentType contentType,
                                    String comment) {
        ApprovalRequest req = new ApprovalRequest();
        req.setContentId(contentId);
        req.setContentType(contentType);
        req.setStatus(ApprovalStatus.REJECTED);
        req.setComment(comment);
        return create(req);
    }

    // ==================== PRIVATE ====================

    /**
     * Đồng bộ status sang entity gốc sau khi tạo approval record.
     */
    private void applyStatusToEntity(Long contentId,
                                      ApprovalContentType contentType,
                                      ApprovalStatus status) {
        switch (contentType) {
            case QUESTION -> {
                Question q = questionRepository.findById(contentId)
                        .orElseThrow(() -> new IllegalArgumentException("Question not found: " + contentId));
                q.setStatus(status == ApprovalStatus.APPROVED
                        ? QuestionStatus.APPROVED
                        : QuestionStatus.REJECTED);
                questionRepository.save(q);
            }
            case LESSON_PLAN -> {
                LessonPlanTemplate t = templateRepository.findById(contentId)
                        .orElseThrow(() -> new IllegalArgumentException("LessonPlanTemplate not found: " + contentId));
                // Chỉ set APPROVED khi được duyệt; rejected giữ PENDING để teacher sửa lại
                if (status == ApprovalStatus.APPROVED) {
                    t.setStatus(TemplateStatus.APPROVED);
                    templateRepository.save(t);
                }
            }
            case PROMPT -> {
                // PromptTemplate không có status workflow hiện tại — bỏ qua
            }
            default -> throw new IllegalArgumentException("Unsupported contentType: " + contentType);
        }
    }

    private ApprovalResponse toResponse(Approval a) {
        return ApprovalResponse.builder()
                .approvalId(a.getApprovalId())
                .approverId(a.getApprover().getUserId())
                .approverName(a.getApprover().getFullName())
                .contentId(a.getContentId())
                .contentType(a.getContentType())
                .status(a.getStatus())
                .comment(a.getComment())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
