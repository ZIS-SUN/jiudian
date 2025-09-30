package com.hotel.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("PENDING", "待确认"),
    CONFIRMED("CONFIRMED", "已确认"),
    CHECKED_IN("CHECKED_IN", "已入住"),
    CHECKED_OUT("CHECKED_OUT", "已退房"),
    CANCELLED("CANCELLED", "已取消");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    ReservationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
