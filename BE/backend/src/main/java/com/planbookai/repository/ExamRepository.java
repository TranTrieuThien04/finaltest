package com.planbookai.repository;

import com.planbookai.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions")
    List<Exam> findAllWithQuestions();
}