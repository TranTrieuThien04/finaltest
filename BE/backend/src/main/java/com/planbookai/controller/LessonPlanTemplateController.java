package com.planbookai.controller;

import com.planbookai.dto.lessonplan.LessonPlanTemplateRequest;
import com.planbookai.dto.lessonplan.LessonPlanTemplateResponse;
import com.planbookai.service.LessonPlanTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson-plan-templates")
@RequiredArgsConstructor
public class LessonPlanTemplateController {

    private final LessonPlanTemplateService lessonPlanTemplateService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','STAFF','ADMIN')")
    public List<LessonPlanTemplateResponse> list() {
        return lessonPlanTemplateService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<LessonPlanTemplateResponse> create(@Valid @RequestBody LessonPlanTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonPlanTemplateService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public LessonPlanTemplateResponse update(@PathVariable @NonNull Long id, @Valid @RequestBody LessonPlanTemplateRequest request) {
        return lessonPlanTemplateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        lessonPlanTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
