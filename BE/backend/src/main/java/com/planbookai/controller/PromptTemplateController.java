package com.planbookai.controller;

import com.planbookai.dto.prompt.PromptTemplateRequest;
import com.planbookai.dto.prompt.PromptTemplateResponse;
import com.planbookai.service.PromptTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptTemplateController {

    private final PromptTemplateService promptTemplateService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','TEACHER')")
    public List<PromptTemplateResponse> list() {
        return promptTemplateService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<PromptTemplateResponse> create(@Valid @RequestBody PromptTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promptTemplateService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public PromptTemplateResponse update(@PathVariable @NonNull Long id, @Valid @RequestBody PromptTemplateRequest request) {
        return promptTemplateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        promptTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
