package com.planbookai.controller;

import com.planbookai.dto.SubjectDTO;
import com.planbookai.dto.TopicDTO;
import com.planbookai.entity.Subject;
import com.planbookai.repository.SubjectRepository;
import com.planbookai.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubjectDTO>> listAll() {
        List<SubjectDTO> dtos = subjectRepository.findAll().stream()
                .map(s -> new SubjectDTO(s.getSubjectId(), s.getName()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubjectDTO> getById(@PathVariable @NonNull Long id) {
        return subjectRepository.findById(id)
                .map(s -> ResponseEntity.ok(new SubjectDTO(s.getSubjectId(), s.getName())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/topics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TopicDTO>> topicsBySubject(@PathVariable @NonNull Long id) {
        List<TopicDTO> dtos = topicRepository.findBySubject_SubjectId(id).stream()
                .map(t -> new TopicDTO(t.getTopicId(), t.getName(), t.getSubject().getSubjectId()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<SubjectDTO> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Subject subject = new Subject();
        subject.setName(name.trim());
        Subject saved = subjectRepository.save(subject);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubjectDTO(saved.getSubjectId(), saved.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDTO> update(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, String> body) {
        return subjectRepository.findById(id).map(s -> {
            s.setName(body.get("name").trim());
            Subject updated = subjectRepository.save(s);
            return ResponseEntity.ok(new SubjectDTO(updated.getSubjectId(), updated.getName()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        if (!subjectRepository.existsById(id)) return ResponseEntity.notFound().build();
        subjectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}