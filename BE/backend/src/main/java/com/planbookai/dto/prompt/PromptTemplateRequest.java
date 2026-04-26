package com.planbookai.dto.prompt;

import com.planbookai.entity.enums.PromptTemplateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptTemplateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private PromptTemplateType type;
}
