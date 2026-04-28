package com.planbookai.dto.ocr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerKeyRequest {

    @NotNull(message = "examId không được để trống")
    private Long examId;

    @NotBlank(message = "examCode không được để trống")
    private String examCode;

    /**
     * JSON đáp án chuẩn.
     * Ví dụ: {"part_1":["A","B","C","D","A","B","C","D","A","B"]}
     * Số phần tử trong part_1 = tổng số câu trắc nghiệm
     */
    @NotBlank(message = "answersJson không được để trống")
    private String answersJson;
}