package com.planbookai.controller;

import com.planbookai.dto.lessonplan.LessonPlanResponse;
import com.planbookai.service.LessonPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lesson-plans")
@RequiredArgsConstructor
public class LessonPlanController {

    private final LessonPlanService lessonPlanService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<LessonPlanResponse> listMine(@RequestParam(required = false) String status) {
        return lessonPlanService.listMine(status);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public LessonPlanResponse updateStatus(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, String> body) {
        return lessonPlanService.updateStatus(id, body.get("status"));
    }
}