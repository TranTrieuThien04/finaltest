package com.planbookai.dto.exercise;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseCreateRequest {

    @NotBlank
    private String title;

    @Min(1)
    private int questionCount;

    private Long topicId;
}
