package com.planbookai.repository;

import com.planbookai.entity.ExerciseQuestion;
import com.planbookai.entity.ExerciseQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseQuestionRepository extends JpaRepository<ExerciseQuestion, ExerciseQuestionId> {

    List<ExerciseQuestion> findByExercise_ExerciseId(Long exerciseId);
}
