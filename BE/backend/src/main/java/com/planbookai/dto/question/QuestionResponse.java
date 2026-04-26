package com.planbookai.dto.question;

import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Outward view of a question for REST responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long questionId;
    private Long topicId;
    private String topicName;
    private Long createdByUserId;
    private String content;
    private QuestionType type;
    private QuestionDifficulty difficulty;
    private QuestionStatus status;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<QuestionChoiceResponse> choices = new ArrayList<>();
}
