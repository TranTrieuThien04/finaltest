package com.planbookai.dto.lessonplan;

import com.planbookai.entity.enums.TemplateStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LessonPlanTemplateResponse {
    private Long lessonPlanTemplateId;
    private String title;
    private String structureJson;
    private TemplateStatus status;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}
