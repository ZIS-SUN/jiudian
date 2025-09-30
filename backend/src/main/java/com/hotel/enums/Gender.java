package com.hotel.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("MALE", "男"),
    FEMALE("FEMALE", "女");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
