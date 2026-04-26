package com.planbookai.entity.converter;

import com.planbookai.entity.enums.QuestionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class QuestionStatusConverter implements AttributeConverter<QuestionStatus, String> {
    @Override
    public String convertToDatabaseColumn(QuestionStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public QuestionStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : QuestionStatus.valueOf(dbData.toUpperCase());
    }
}
