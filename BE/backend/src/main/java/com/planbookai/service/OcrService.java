package com.planbookai.service;

import com.planbookai.dto.ocr.OcrResultResponse;
import com.planbookai.dto.ocr.OcrSimulateRequest;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OcrService {
    OcrResultResponse simulateAndSave(OcrSimulateRequest request);

    List<OcrResultResponse> findByExam(@NonNull Long examId);
}
