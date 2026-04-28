package com.planbookai.service;

import com.planbookai.dto.lessonplan.LessonPlanResponse;
import java.util.List;

public interface LessonPlanService {
    List<LessonPlanResponse> listMine(String status);
    LessonPlanResponse updateStatus(Long id, String status);
}