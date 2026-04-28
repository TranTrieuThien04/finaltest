package com.planbookai.dto.ocr;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnswerKeyResponse {

    private Long answerKeyId;
    private Long examId;
    private String examCode;
    private String answersJson;
    private LocalDateTime createdAt;
}