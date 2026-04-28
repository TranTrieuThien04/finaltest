package com.planbookai.dto.approval;

import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApprovalResponse {

    private Long approvalId;
    private Long approverId;
    private String approverName;
    private Long contentId;
    private ApprovalContentType contentType;
    private ApprovalStatus status;
    private String comment;
    private LocalDateTime createdAt;
}
