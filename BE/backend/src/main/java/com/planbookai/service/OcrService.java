package com.planbookai.service;

import com.planbookai.dto.ocr.OcrResultResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface OcrService {
    /** Raw OCR — chỉ trích xuất text, không lưu DB */
    String processImage(MultipartFile file) throws Exception;

    /** FIX: Full flow — OCR + so đáp án + tính điểm + lưu DB */
    OcrResultResponse gradeAndSave(MultipartFile file, Long examId, String studentName) throws Exception;

    List<OcrResultResponse> findByExam(@NonNull Long examId);
}