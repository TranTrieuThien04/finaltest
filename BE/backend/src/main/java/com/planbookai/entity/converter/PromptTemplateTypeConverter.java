package com.planbookai.entity.converter;

import com.planbookai.entity.enums.PromptTemplateType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PromptTemplateTypeConverter implements AttributeConverter<PromptTemplateType, String> {
    @Override
    public String convertToDatabaseColumn(PromptTemplateType attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public PromptTemplateType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PromptTemplateType.valueOf(dbData.toUpperCase());
    }
}
