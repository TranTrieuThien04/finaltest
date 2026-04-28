package com.planbookai.repository;

import com.planbookai.entity.AnswerKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerKeyRepository extends JpaRepository<AnswerKey, Long> {

    Optional<AnswerKey> findByExamCode(String examCode);

    Optional<AnswerKey> findByExam_ExamId(Long examId);

    boolean existsByExamCode(String examCode);
}