package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stored output of OCR-based grading for one exam attempt / paper.
 */
@Entity
@Table(name = "ocr_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OCRResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocr_result_id")
    private Long ocrResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "student_name", length = 255)
    private String studentName;

    private Double score;

    /** Raw OCR / grading payload as JSON text. */
    @Column(name = "result_json", columnDefinition = "json")
    private String resultJson;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
