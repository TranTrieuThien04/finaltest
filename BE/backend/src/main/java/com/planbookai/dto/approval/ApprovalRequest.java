package com.planbookai.dto.approval;

import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {

    @NotNull(message = "contentId không được null")
    private Long contentId;

    @NotNull(message = "contentType không được null")
    private ApprovalContentType contentType;

    @NotNull(message = "status không được null")
    private ApprovalStatus status;

    private String comment;
}
