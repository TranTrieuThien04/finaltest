package com.planbookai.controller;

import com.planbookai.dto.lessonplan.LessonPlanRequest;
import com.planbookai.dto.lessonplan.LessonPlanResponse;
import com.planbookai.service.LessonPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson-plans")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public class LessonPlanController {

    private final LessonPlanService lessonPlanService;

    @PostMapping
    public ResponseEntity<LessonPlanResponse> create(@Valid @RequestBody LessonPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonPlanService.create(request));
    }

    @GetMapping
    public List<LessonPlanResponse> listMine() {
        return lessonPlanService.listMine();
    }

    @GetMapping("/{id}")
    public LessonPlanResponse get(@PathVariable @NonNull Long id) {
        return lessonPlanService.getById(id);
    }

    @PutMapping("/{id}")
    public LessonPlanResponse update(@PathVariable @NonNull Long id, @Valid @RequestBody LessonPlanRequest request) {
        return lessonPlanService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        lessonPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
