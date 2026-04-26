package com.planbookai.service.impl;

import com.planbookai.dto.prompt.PromptTemplateRequest;
import com.planbookai.dto.prompt.PromptTemplateResponse;
import com.planbookai.entity.PromptTemplate;
import com.planbookai.entity.User;
import com.planbookai.repository.PromptTemplateRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.PromptTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PromptTemplateServiceImpl implements PromptTemplateService {

    private final PromptTemplateRepository promptTemplateRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public PromptTemplateResponse create(PromptTemplateRequest request) {
        Long currentUserId = currentUserService.requireUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));
        PromptTemplate entity = new PromptTemplate();
        entity.setCreatedBy(user);
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setCreatedAt(LocalDateTime.now());
        return toResponse(promptTemplateRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromptTemplateResponse> findAll() {
        return promptTemplateRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PromptTemplateResponse update(@NonNull Long id, PromptTemplateRequest request) {
        PromptTemplate entity = getPromptTemplate(id);
        ensureOwnerOrAdmin(entity);
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        return toResponse(promptTemplateRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long id) {
        PromptTemplate entity = getPromptTemplate(id);
        ensureOwnerOrAdmin(entity);
        promptTemplateRepository.delete(entity);
    }

    private @NonNull PromptTemplate getPromptTemplate(@NonNull Long id) {
        return Objects.requireNonNull(
                promptTemplateRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Prompt template not found: " + id))
        );
    }

    private void ensureOwnerOrAdmin(PromptTemplate entity) {
        if (currentUserService.hasRole("ADMIN") || currentUserService.hasRole("STAFF")) {
            return;
        }
        if (!currentUserService.requireUserId().equals(entity.getCreatedBy().getUserId())) {
            throw new IllegalArgumentException("You can only modify your own prompt template");
        }
    }

    private PromptTemplateResponse toResponse(PromptTemplate entity) {
        return PromptTemplateResponse.builder()
                .promptTemplateId(entity.getPromptTemplateId())
                .createdByUserId(entity.getCreatedBy().getUserId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
