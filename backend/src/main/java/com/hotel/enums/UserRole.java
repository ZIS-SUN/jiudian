package com.hotel.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    STAFF("STAFF", "员工");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 手动添加getter方法
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
