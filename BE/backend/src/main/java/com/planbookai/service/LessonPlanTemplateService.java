package com.planbookai.service;

import com.planbookai.dto.lessonplan.LessonPlanTemplateRequest;
import com.planbookai.dto.lessonplan.LessonPlanTemplateResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface LessonPlanTemplateService {
    LessonPlanTemplateResponse create(LessonPlanTemplateRequest request);

    List<LessonPlanTemplateResponse> findAll();

    LessonPlanTemplateResponse update(@NonNull Long id, LessonPlanTemplateRequest request);

    void delete(@NonNull Long id);
}
