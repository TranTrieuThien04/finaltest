package com.planbookai.service;

import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<QuestionResponse> findAll();

    Optional<QuestionResponse> findById(@NonNull Long questionId);

    QuestionResponse create(QuestionCreateRequest request);

    QuestionResponse update(@NonNull Long questionId, QuestionUpdateRequest request);

    void delete(@NonNull Long questionId);

    List<QuestionResponse> filter(Long topicId, Long subjectId);
}
