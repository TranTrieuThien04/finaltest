package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Join row linking an exercise to a bank question (N–N).
 */
@Entity
@Table(name = "exercise_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseQuestion {

    @EmbeddedId
    private ExerciseQuestionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
