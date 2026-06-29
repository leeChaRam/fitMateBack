package com.fitmate.fit_mate_server.domain.member;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrivacyOptionConverter implements AttributeConverter<PrivacyOption, Integer>{
    //1. 자바 Enum -> DB 숫자로 바꿀 떄
    @Override
    public Integer convertToDatabaseColumn(PrivacyOption attribute) {
        if (attribute == null) return null;
        return attribute.getDbValue();
    }
    @Override
    public PrivacyOption convertToEntityAttribute(Integer dbData){
        if (dbData == null) return null;
        return PrivacyOption.fromDbValue(dbData);
    }
    
}
