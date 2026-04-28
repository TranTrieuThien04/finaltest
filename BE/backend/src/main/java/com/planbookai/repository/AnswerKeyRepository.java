package com.planbookai.repository;

import com.planbookai.entity.AnswerKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerKeyRepository extends JpaRepository<AnswerKey, Long> {

    Optional<AnswerKey> findByExamCode(String examCode);

    Optional<AnswerKey> findByExam_ExamId(Long examId);

    boolean existsByExamCode(String examCode);

    /** Lấy đáp án của một giáo viên cụ thể (qua exam.teacher) */
    List<AnswerKey> findByExam_Teacher_UserId(Long userId);
}
