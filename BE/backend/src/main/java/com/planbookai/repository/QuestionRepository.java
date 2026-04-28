package com.planbookai.repository;

import com.planbookai.entity.Question;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Lấy câu hỏi theo topic */
    List<Question> findByTopic_TopicId(Long topicId);

    /** Lấy câu hỏi theo topic VÀ status (dùng khi tạo đề thi) */
    List<Question> findByTopic_TopicIdAndStatus(Long topicId, QuestionStatus status);

    /** Lấy câu hỏi theo status */
    List<Question> findByStatus(QuestionStatus status);

    /** Lấy câu hỏi theo người tạo */
    List<Question> findByCreatedBy_UserId(Long userId);

    /** Đếm câu hỏi đã approved trong một topic */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.topicId = :topicId AND q.status = 'APPROVED'")
    long countApprovedByTopic(@Param("topicId") Long topicId);

    /**
     * Filter có phân trang với các tham số tuỳ chọn.
     * Null parameter = bỏ qua điều kiện đó.
     */
    @Query("""
            SELECT q FROM Question q
            WHERE (:topicId    IS NULL OR q.topic.topicId                    = :topicId)
              AND (:subjectId  IS NULL OR q.topic.subject.subjectId          = :subjectId)
              AND (:status     IS NULL OR q.status                           = :status)
              AND (:difficulty IS NULL OR q.difficulty                       = :difficulty)
            """)
    Page<Question> filterPaged(
            @Param("topicId")    Long topicId,
            @Param("subjectId")  Long subjectId,
            @Param("status")     QuestionStatus status,
            @Param("difficulty") QuestionDifficulty difficulty,
            Pageable pageable
    );

    // ---- Analytics queries ----

    @Query("SELECT COUNT(q) FROM Question q WHERE q.status = :status")
    long countByStatus(@Param("status") QuestionStatus status);
}
