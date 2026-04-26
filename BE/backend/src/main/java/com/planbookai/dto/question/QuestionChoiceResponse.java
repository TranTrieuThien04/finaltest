package com.planbookai.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Safe outward view of an MCQ option (API output).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionChoiceResponse {

    private Long questionChoiceId;
    private String content;
    private boolean correct;
}
