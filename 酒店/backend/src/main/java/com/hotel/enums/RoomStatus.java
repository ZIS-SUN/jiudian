package com.hotel.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RoomStatus {
    AVAILABLE("AVAILABLE", "可用"),
    OCCUPIED("OCCUPIED", "已入住"),
    MAINTENANCE("MAINTENANCE", "维护中"),
    OUT_OF_ORDER("OUT_OF_ORDER", "故障");

    @EnumValue
    private final String code;

    @JsonValue
    private final String description;

    RoomStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
