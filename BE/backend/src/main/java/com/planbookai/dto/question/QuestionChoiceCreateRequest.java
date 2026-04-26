package com.planbookai.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Input for a single MCQ option when creating a question.
 */
@Getter
@Setter
public class QuestionChoiceCreateRequest {

    @NotBlank
    private String content;

    @NotNull
    private Boolean correct;
}
