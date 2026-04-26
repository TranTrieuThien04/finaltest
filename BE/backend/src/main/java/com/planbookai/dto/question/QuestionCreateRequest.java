package com.planbookai.dto.question;

import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload to create a bank question (API input; not a JPA entity).
 */
@Getter
@Setter
public class QuestionCreateRequest {

    @NotNull
    private Long topicId;

    @NotBlank
    private String content;

    @NotNull
    private QuestionType type;

    @NotNull
    private QuestionDifficulty difficulty;

    @Valid
    private List<QuestionChoiceCreateRequest> choices = new ArrayList<>();
}
