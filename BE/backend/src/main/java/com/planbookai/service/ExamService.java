package com.planbookai.service;

import com.planbookai.dto.exam.ExamCreateRequest;
import com.planbookai.dto.exam.ExamResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ExamService {
    ExamResponse create(ExamCreateRequest request);

    List<ExamResponse> listMine();

    ExamResponse getById(@NonNull Long examId);

    void delete(@NonNull Long examId);
}
