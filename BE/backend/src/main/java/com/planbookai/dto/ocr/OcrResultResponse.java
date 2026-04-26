package com.planbookai.dto.ocr;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OcrResultResponse {
    private Long ocrResultId;
    private Long examId;
    private String studentName;
    private Double score;
    private String resultJson;
    private LocalDateTime gradedAt;
}
