package com.planbookai.repository;

import com.planbookai.entity.Question;
import com.planbookai.entity.enums.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Lấy câu hỏi theo topic */
    List<Question> findByTopic_TopicId(Long topicId);

    /** Lấy câu hỏi theo topic VÀ status (dùng khi tạo đề thi) */
    List<Question> findByTopic_TopicIdAndStatus(Long topicId, QuestionStatus status);

    /** Lấy câu hỏi theo status (dùng khi tạo đề thi — không filter topic) */
    List<Question> findByStatus(QuestionStatus status);

    /** Lấy câu hỏi theo người tạo */
    List<Question> findByCreatedBy_UserId(Long userId);

    /** Đếm câu hỏi đã approved trong một topic */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.topicId = :topicId AND q.status = 'APPROVED'")
    long countApprovedByTopic(@Param("topicId") Long topicId);
}