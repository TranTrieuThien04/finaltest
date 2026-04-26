package com.planbookai.service;

import com.planbookai.dto.exercise.ExerciseCreateRequest;
import com.planbookai.dto.exercise.ExerciseResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ExerciseService {
    ExerciseResponse create(ExerciseCreateRequest request);

    List<ExerciseResponse> listMine();

    ExerciseResponse getById(@NonNull Long exerciseId);

    void delete(@NonNull Long exerciseId);
}
