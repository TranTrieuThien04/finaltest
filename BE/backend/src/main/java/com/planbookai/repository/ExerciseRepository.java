package com.planbookai.repository;

import com.planbookai.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByTeacher_UserId(Long teacherId);
}
