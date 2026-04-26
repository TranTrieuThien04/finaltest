package com.planbookai.entity.converter;

import com.planbookai.entity.enums.TemplateStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TemplateStatusConverter implements AttributeConverter<TemplateStatus, String> {
    @Override
    public String convertToDatabaseColumn(TemplateStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public TemplateStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TemplateStatus.valueOf(dbData.toUpperCase());
    }
}
