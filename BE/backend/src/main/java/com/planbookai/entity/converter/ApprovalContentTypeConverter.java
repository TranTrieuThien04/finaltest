package com.planbookai.entity.converter;

import com.planbookai.entity.enums.ApprovalContentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ApprovalContentTypeConverter implements AttributeConverter<ApprovalContentType, String> {
    @Override
    public String convertToDatabaseColumn(ApprovalContentType attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public ApprovalContentType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ApprovalContentType.valueOf(dbData.toUpperCase());
    }
}
