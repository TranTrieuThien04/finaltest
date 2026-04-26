package com.planbookai.controller;

import com.planbookai.dto.exercise.ExerciseCreateRequest;
import com.planbookai.dto.exercise.ExerciseResponse;
import com.planbookai.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.create(request));
    }

    @GetMapping
    public List<ExerciseResponse> listMine() {
        return exerciseService.listMine();
    }

    @GetMapping("/{id}")
    public ExerciseResponse get(@PathVariable @NonNull Long id) {
        return exerciseService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
