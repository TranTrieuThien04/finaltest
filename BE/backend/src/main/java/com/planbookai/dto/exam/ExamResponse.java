package com.planbookai.dto.exam;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ExamResponse {
    private Long examId;
    private String title;
    private Integer duration;
    private Integer totalQuestions;
    private Long teacherId;
    private LocalDateTime createdAt;
    private List<Long> questionIds;
}
