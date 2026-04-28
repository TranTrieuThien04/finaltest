package com.planbookai.repository;

import com.planbookai.entity.ExamQuestion;
import com.planbookai.entity.ExamQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {

    List<ExamQuestion> findByExam_ExamIdOrderByOrderIndexAsc(Long examId);

    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.exam.examId = :examId")
    void deleteByExam_ExamId(@Param("examId") Long examId);
}