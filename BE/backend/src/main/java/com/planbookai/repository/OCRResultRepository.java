package com.planbookai.repository;

import com.planbookai.entity.OcrResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcrResultRepository extends JpaRepository<OcrResult, Long> {

    List<OcrResult> findByExamId(Long examId);

    List<OcrResult> findByExamIdOrderByGradedAtDesc(Long examId);
}