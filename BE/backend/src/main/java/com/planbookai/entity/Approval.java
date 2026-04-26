package com.planbookai.entity;

import com.planbookai.entity.converter.ApprovalContentTypeConverter;
import com.planbookai.entity.converter.ApprovalStatusConverter;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Review decision for a logical content row ({@code contentId} + {@code contentType}).
 */
@Entity
@Table(name = "approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private User approver;

    /** Primary key of the target entity (polymorphic; not a JPA FK). */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Convert(converter = ApprovalContentTypeConverter.class)
    @Column(name = "content_type", nullable = false, length = 50)
    private ApprovalContentType contentType;

    @Convert(converter = ApprovalStatusConverter.class)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
