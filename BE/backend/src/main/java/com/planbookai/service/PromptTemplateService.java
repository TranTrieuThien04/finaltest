package com.planbookai.service;

import com.planbookai.dto.prompt.PromptTemplateRequest;
import com.planbookai.dto.prompt.PromptTemplateResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface PromptTemplateService {
    PromptTemplateResponse create(PromptTemplateRequest request);

    List<PromptTemplateResponse> findAll();

    PromptTemplateResponse update(@NonNull Long id, PromptTemplateRequest request);

    void delete(@NonNull Long id);
}
