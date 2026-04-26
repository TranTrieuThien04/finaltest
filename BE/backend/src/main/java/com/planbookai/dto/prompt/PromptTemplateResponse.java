package com.planbookai.dto.prompt;

import com.planbookai.entity.enums.PromptTemplateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PromptTemplateResponse {
    private Long promptTemplateId;
    private Long createdByUserId;
    private String title;
    private String content;
    private PromptTemplateType type;
    private LocalDateTime createdAt;
}
