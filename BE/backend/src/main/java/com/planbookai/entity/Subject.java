package com.planbookai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * High-level subject area for organizing topics (e.g. Chemistry).
 */
@Entity
@Table(name = "subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(length = 255)
    private String name;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();
}
