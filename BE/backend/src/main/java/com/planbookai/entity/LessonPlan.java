package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Concrete lesson instance derived from a template; body in JSON string.
 */
@Entity
@Table(name = "lesson_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_plan_id")
    private Long lessonPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private LessonPlanTemplate template;

    @Column(length = 255)
    private String title;

    @Column(name = "content_json", columnDefinition = "json")
    private String contentJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
