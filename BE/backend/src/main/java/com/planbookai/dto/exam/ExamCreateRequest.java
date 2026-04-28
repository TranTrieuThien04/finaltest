package com.planbookai.dto.exam;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamCreateRequest {

    @NotBlank(message = "Tiêu đề đề thi không được để trống")
    private String title;

    @Min(value = 1, message = "Thời gian thi phải ít nhất 1 phút")
    private Integer duration = 45;

    @Min(value = 1, message = "Số câu hỏi phải ít nhất 1")
    private Integer totalQuestions;

    /** Lọc theo chủ đề (tùy chọn — nếu null thì lấy tất cả môn) */
    private Long topicId;

    /**
     * Chọn câu hỏi cụ thể từ ngân hàng (tùy chọn).
     * Nếu có, hệ thống dùng list này thay vì random.
     * Số lượng phải bằng totalQuestions.
     */
    private List<Long> questionIds;
}