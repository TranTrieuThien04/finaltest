package com.planbookai.repository;

import com.planbookai.entity.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {
    List<LessonPlan> findByTeacher_UserId(Long userId);
    List<LessonPlan> findByStatus(String status);
}