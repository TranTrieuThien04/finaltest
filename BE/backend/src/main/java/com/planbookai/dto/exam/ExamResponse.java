package com.planbookai.dto.exam;

import com.planbookai.dto.question.QuestionResponse;
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

    /** Danh sách ID câu hỏi (luôn có) */
    private List<Long> questionIds;

    /**
     * Nội dung đầy đủ của câu hỏi (chỉ có khi gọi GET /exams/{id})
     * null khi gọi GET /exams (list)
     */
    private List<QuestionResponse> questions;
}