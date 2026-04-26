package com.planbookai.service.impl;

import com.planbookai.dto.lessonplan.LessonPlanRequest;
import com.planbookai.dto.lessonplan.LessonPlanResponse;
import com.planbookai.entity.LessonPlan;
import com.planbookai.entity.LessonPlanTemplate;
import com.planbookai.entity.User;
import com.planbookai.repository.LessonPlanRepository;
import com.planbookai.repository.LessonPlanTemplateRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.LessonPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LessonPlanServiceImpl implements LessonPlanService {

    private final LessonPlanRepository lessonPlanRepository;
    private final LessonPlanTemplateRepository lessonPlanTemplateRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public LessonPlanResponse create(LessonPlanRequest request) {
        Long currentUserId = Objects.requireNonNull(currentUserService.requireUserId());
        Long templateId = Objects.requireNonNull(request.getTemplateId(), "templateId is required");
        User teacher = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));
        LessonPlanTemplate template = lessonPlanTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        LessonPlan lessonPlan = new LessonPlan();
        lessonPlan.setTeacher(teacher);
        lessonPlan.setTemplate(template);
        lessonPlan.setTitle(request.getTitle());
        lessonPlan.setContentJson(request.getContentJson());
        lessonPlan.setCreatedAt(LocalDateTime.now());
        return toResponse(lessonPlanRepository.save(lessonPlan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonPlanResponse> listMine() {
        if (currentUserService.hasRole("ADMIN")) {
            return lessonPlanRepository.findAll().stream().map(this::toResponse).toList();
        }
        return lessonPlanRepository.findByTeacher_UserId(currentUserService.requireUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LessonPlanResponse getById(@NonNull Long lessonPlanId) {
        LessonPlan lessonPlan = getLessonPlan(lessonPlanId);
        ensureOwnerOrAdmin(lessonPlan);
        return toResponse(lessonPlan);
    }

    @Override
    @Transactional
    public LessonPlanResponse update(@NonNull Long lessonPlanId, LessonPlanRequest request) {
        LessonPlan lessonPlan = getLessonPlan(lessonPlanId);
        ensureOwnerOrAdmin(lessonPlan);
        Long templateId = Objects.requireNonNull(request.getTemplateId(), "templateId is required");
        LessonPlanTemplate template = lessonPlanTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
        lessonPlan.setTemplate(template);
        lessonPlan.setTitle(request.getTitle());
        lessonPlan.setContentJson(request.getContentJson());
        return toResponse(lessonPlanRepository.save(lessonPlan));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long lessonPlanId) {
        LessonPlan lessonPlan = getLessonPlan(lessonPlanId);
        ensureOwnerOrAdmin(lessonPlan);
        lessonPlanRepository.delete(lessonPlan);
    }

    private @NonNull LessonPlan getLessonPlan(@NonNull Long lessonPlanId) {
        return Objects.requireNonNull(
                lessonPlanRepository.findById(lessonPlanId)
                        .orElseThrow(() -> new IllegalArgumentException("Lesson plan not found: " + lessonPlanId))
        );
    }

    private void ensureOwnerOrAdmin(@NonNull LessonPlan lessonPlan) {
        if (currentUserService.hasRole("ADMIN")) {
            return;
        }
        if (!currentUserService.requireUserId().equals(lessonPlan.getTeacher().getUserId())) {
            throw new IllegalArgumentException("You can only access your own lesson plan");
        }
    }

    private LessonPlanResponse toResponse(LessonPlan lessonPlan) {
        return LessonPlanResponse.builder()
                .lessonPlanId(lessonPlan.getLessonPlanId())
                .teacherId(lessonPlan.getTeacher().getUserId())
                .templateId(lessonPlan.getTemplate().getLessonPlanTemplateId())
                .title(lessonPlan.getTitle())
                .contentJson(lessonPlan.getContentJson())
                .createdAt(lessonPlan.getCreatedAt())
                .build();
    }
}
