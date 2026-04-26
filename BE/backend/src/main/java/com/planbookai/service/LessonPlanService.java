package com.planbookai.service;

import com.planbookai.dto.lessonplan.LessonPlanRequest;
import com.planbookai.dto.lessonplan.LessonPlanResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface LessonPlanService {
    LessonPlanResponse create(LessonPlanRequest request);

    List<LessonPlanResponse> listMine();

    LessonPlanResponse getById(@NonNull Long lessonPlanId);

    LessonPlanResponse update(@NonNull Long lessonPlanId, LessonPlanRequest request);

    void delete(@NonNull Long lessonPlanId);
}
