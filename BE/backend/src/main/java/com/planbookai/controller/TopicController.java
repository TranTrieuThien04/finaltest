package com.planbookai.controller;

import com.planbookai.entity.Subject;
import com.planbookai.entity.Topic;
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
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    /** Lấy tất cả topics, có thể filter theo subjectId */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Topic> listAll(
            @RequestParam(required = false) Long subjectId) {
        if (subjectId != null) {
            return topicRepository.findBySubject_SubjectId(subjectId);
        }
        return topicRepository.findAll();
    }

    /** Lấy topic theo ID */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Topic> getById(@PathVariable @NonNull Long id) {
        return topicRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Tạo topic mới — ADMIN, STAFF */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Object subjectIdRaw = body.get("subjectId");
        if (name == null || name.isBlank() || subjectIdRaw == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "name và subjectId là bắt buộc"));
        }
        Long subjectId = Long.parseLong(subjectIdRaw.toString());
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

        Topic topic = new Topic();
        topic.setName(name.trim());
        topic.setSubject(subject);
        return ResponseEntity.status(HttpStatus.CREATED).body(topicRepository.save(topic));
    }

    /** Cập nhật topic — ADMIN */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, Object> body) {
        return topicRepository.findById(id).map(t -> {
            if (body.containsKey("name")) {
                t.setName(((String) body.get("name")).trim());
            }
            if (body.containsKey("subjectId")) {
                Long subjectId = Long.parseLong(body.get("subjectId").toString());
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
                t.setSubject(subject);
            }
            return ResponseEntity.ok(topicRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Xóa topic — ADMIN */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        if (!topicRepository.existsById(id)) return ResponseEntity.notFound().build();
        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}