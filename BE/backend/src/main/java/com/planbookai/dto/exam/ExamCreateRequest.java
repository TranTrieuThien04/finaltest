package com.planbookai.dto.exam;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamCreateRequest {

    @NotBlank
    private String title;

    @Min(1)
    private Integer duration;

    @Min(1)
    private Integer totalQuestions;

    private Long topicId;
}
