package com.planbookai.dto.lessonplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonPlanRequest {

    @NotNull
    private Long templateId;

    @NotBlank
    private String title;

    @NotBlank
    private String contentJson;
}
