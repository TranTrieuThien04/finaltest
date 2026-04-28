package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class LessonPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String grade;
    
    private String teacherName;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Trạng thái mặc định khi giáo viên vừa tạo
    private String status = "PENDING"; 

    private LocalDateTime createdAt = LocalDateTime.now();
}