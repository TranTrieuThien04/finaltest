package com.planbookai.entity;

import com.planbookai.entity.converter.TemplateStatusConverter;
import com.planbookai.entity.enums.TemplateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable lesson skeleton; structure stored as JSON string.
 */
@Entity
@Table(name = "lesson_plan_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonPlanTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_plan_template_id")
    private Long lessonPlanTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(length = 255)
    private String title;

    /** Outline / sections definition (JSON as text). */
    @Column(name = "structure_json", columnDefinition = "json")
    private String structureJson;

    @Convert(converter = TemplateStatusConverter.class)
    @Column(nullable = false, length = 20)
    private TemplateStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<LessonPlan> lessonPlans = new ArrayList<>();
}
