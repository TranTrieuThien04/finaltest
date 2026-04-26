package com.planbookai.entity.converter;

import com.planbookai.entity.enums.QuestionDifficulty;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class QuestionDifficultyConverter implements AttributeConverter<QuestionDifficulty, String> {
    @Override
    public String convertToDatabaseColumn(QuestionDifficulty attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public QuestionDifficulty convertToEntityAttribute(String dbData) {
        return dbData == null ? null : QuestionDifficulty.valueOf(dbData.toUpperCase());
    }
}
