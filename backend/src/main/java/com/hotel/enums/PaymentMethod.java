package com.hotel.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("CASH", "现金"),
    CARD("CARD", "银行卡"),
    ALIPAY("ALIPAY", "支付宝"),
    WECHAT("WECHAT", "微信支付"),
    BANK_TRANSFER("BANK_TRANSFER", "银行转账");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    PaymentMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
