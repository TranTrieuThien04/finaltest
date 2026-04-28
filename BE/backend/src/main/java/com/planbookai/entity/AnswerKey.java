package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answer_key")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnswerKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_key_id")
    private Long answerKeyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    /** Mã đề thi, dùng để tra cứu khi chấm OCR (ví dụ: "EXAM001") */
    @Column(name = "exam_code", nullable = false, unique = true, length = 50)
    private String examCode;

    /**
     * Đáp án chuẩn dạng JSON.
     * Ví dụ: {"part_1":["A","B","C","D"], "part_2":["Đ","S","Đ"], "part_3":["12.5"]}
     */
    @Column(name = "answers_json", columnDefinition = "json", nullable = false)
    private String answersJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}