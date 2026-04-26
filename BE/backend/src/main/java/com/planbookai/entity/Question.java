package com.planbookai.entity;

import com.planbookai.entity.converter.QuestionDifficultyConverter;
import com.planbookai.entity.converter.QuestionStatusConverter;
import com.planbookai.entity.converter.QuestionTypeConverter;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.entity.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One assessment item in the bank (stem, type, difficulty, moderation status).
 */
@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /** Teacher or author who created this row. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Convert(converter = QuestionTypeConverter.class)
    @Column(nullable = false, length = 30)
    private QuestionType type;

    @Convert(converter = QuestionDifficultyConverter.class)
    @Column(nullable = false, length = 20)
    private QuestionDifficulty difficulty;

    @Convert(converter = QuestionStatusConverter.class)
    @Column(nullable = false, length = 20)
    private QuestionStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionChoice> choices = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<ExamQuestion> examLinks = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<ExerciseQuestion> exerciseLinks = new ArrayList<>();
}
