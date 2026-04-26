package com.planbookai.repository;

import com.planbookai.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByTeacher_UserId(Long teacherId);
}
