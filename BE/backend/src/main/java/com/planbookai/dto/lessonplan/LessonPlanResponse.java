package com.planbookai.dto.lessonplan;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LessonPlanResponse {
    private Long lessonPlanId;
    private Long teacherId;
    private Long templateId;
    private String title;
    private String contentJson;
    private LocalDateTime createdAt;
}
