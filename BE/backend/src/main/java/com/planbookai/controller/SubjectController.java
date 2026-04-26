package com.planbookai.controller;

import com.planbookai.entity.Subject;
import com.planbookai.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Subject> list() {
        return subjectRepository.findAll();
    }
}