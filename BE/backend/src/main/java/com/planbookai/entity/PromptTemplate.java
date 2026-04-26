package com.planbookai.entity;

import com.planbookai.entity.converter.PromptTemplateTypeConverter;
import com.planbookai.entity.enums.PromptTemplateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Reusable AI system / user prompt text, categorized by purpose.
 */
@Entity
@Table(name = "prompt_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_template_id")
    private Long promptTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Convert(converter = PromptTemplateTypeConverter.class)
    @Column(nullable = false, length = 30)
    private PromptTemplateType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
