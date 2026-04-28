package com.planbookai.service;

import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<QuestionResponse> findAll();

    Optional<QuestionResponse> findById(Long questionId);

    QuestionResponse create(QuestionCreateRequest request);

    QuestionResponse update(Long questionId, QuestionUpdateRequest request);

    void delete(Long questionId);

    /** Filter không phân trang — giữ để tương thích ngược */
    List<QuestionResponse> filter(Long topicId, Long subjectId,
                                   QuestionStatus status, QuestionDifficulty difficulty);

    /**
     * Filter có phân trang — dùng cho list API production.
     * Trả về Page với metadata (totalElements, totalPages, ...).
     */
    Page<QuestionResponse> filterPaged(Long topicId, Long subjectId,
                                        QuestionStatus status, QuestionDifficulty difficulty,
                                        Pageable pageable);

    /** Duyệt câu hỏi (PENDING → APPROVED) + ghi audit */
    QuestionResponse approve(@NonNull Long questionId);

    /** Từ chối câu hỏi + ghi audit */
    QuestionResponse reject(@NonNull Long questionId);
}
