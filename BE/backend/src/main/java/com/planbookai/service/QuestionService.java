package com.planbookai.service;

import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<QuestionResponse> findAll();

    Optional<QuestionResponse> findById(Long questionId);

    QuestionResponse create(QuestionCreateRequest request);

    QuestionResponse update(Long questionId, QuestionUpdateRequest request);

    void delete(Long questionId);

    /**
     * Filter câu hỏi theo topicId, subjectId, status, difficulty
     * Bất kỳ tham số nào null thì bỏ qua điều kiện đó
     */
    List<QuestionResponse> filter(Long topicId, Long subjectId,
                                   QuestionStatus status, QuestionDifficulty difficulty);

    /** Duyệt câu hỏi (PENDING → APPROVED) */
    QuestionResponse approve(Long questionId);

    /** Từ chối câu hỏi */
    QuestionResponse reject(Long questionId);
}