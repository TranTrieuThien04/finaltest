package com.planbookai.service.impl;

import com.planbookai.dto.lessonplan.LessonPlanResponse;
import com.planbookai.entity.LessonPlan;
import com.planbookai.repository.LessonPlanRepository;
import com.planbookai.service.LessonPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonPlanServiceImpl implements LessonPlanService {

    private final LessonPlanRepository lessonPlanRepository;

    @Override
    public List<LessonPlanResponse> listMine(String status) {
        if (status != null && !status.isBlank()) {
            return lessonPlanRepository.findByStatus(status).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return lessonPlanRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public LessonPlanResponse updateStatus(Long id, String status) {
        LessonPlan lessonPlan = lessonPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson plan not found"));
        lessonPlan.setStatus(status.toUpperCase());
        return toResponse(lessonPlanRepository.save(lessonPlan));
    }

    private LessonPlanResponse toResponse(LessonPlan plan) {
        return LessonPlanResponse.builder()
                .lessonPlanId(plan.getId())
                .name(plan.getName())
                .grade(plan.getGrade())
                .teacherName(plan.getTeacherName())
                .content(plan.getContent())
                .status(plan.getStatus())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}