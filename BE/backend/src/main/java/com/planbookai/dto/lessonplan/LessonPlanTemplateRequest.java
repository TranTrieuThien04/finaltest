package com.planbookai.dto.lessonplan;

import com.planbookai.entity.enums.TemplateStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonPlanTemplateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String structureJson;

    @NotNull
    private TemplateStatus status;
}
