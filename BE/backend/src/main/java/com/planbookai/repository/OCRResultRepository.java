package com.planbookai.repository;

import com.planbookai.entity.OCRResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OCRResultRepository extends JpaRepository<OCRResult, Long> {

    List<OCRResult> findByExam_ExamId(Long examId);
}
