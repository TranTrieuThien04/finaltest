package com.planbookai.service;

import com.planbookai.dto.ocr.AnswerKeyRequest;
import com.planbookai.dto.ocr.AnswerKeyResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface AnswerKeyService {

    List<AnswerKeyResponse> findAll();

    AnswerKeyResponse findByExamId(@NonNull Long examId);

    AnswerKeyResponse findByExamCode(String examCode);

    AnswerKeyResponse createOrUpdate(AnswerKeyRequest request);

    void delete(@NonNull Long id);
}
