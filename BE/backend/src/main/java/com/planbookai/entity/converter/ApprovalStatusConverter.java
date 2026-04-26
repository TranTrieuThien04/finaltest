package com.planbookai.entity.converter;

import com.planbookai.entity.enums.ApprovalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ApprovalStatusConverter implements AttributeConverter<ApprovalStatus, String> {
    @Override
    public String convertToDatabaseColumn(ApprovalStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public ApprovalStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ApprovalStatus.valueOf(dbData.toUpperCase());
    }
}
