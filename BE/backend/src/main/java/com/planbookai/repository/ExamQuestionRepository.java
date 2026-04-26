package com.planbookai.repository;

import com.planbookai.entity.ExamQuestion;
import com.planbookai.entity.ExamQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {

    List<ExamQuestion> findByExam_ExamIdOrderByOrderIndexAsc(Long examId);
}
