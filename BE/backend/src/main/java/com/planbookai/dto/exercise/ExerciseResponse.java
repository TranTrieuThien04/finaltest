package com.planbookai.dto.exercise;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ExerciseResponse {
    private Long exerciseId;
    private String title;
    private Long teacherId;
    private LocalDateTime createdAt;
    private List<Long> questionIds;
}
