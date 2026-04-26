package com.planbookai.dto.ocr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrSimulateRequest {

    @NotNull
    private Long examId;

    @NotBlank
    private String studentName;
}
