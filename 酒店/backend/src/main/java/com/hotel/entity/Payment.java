package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.enums.PaymentMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payments")
public class Payment extends BaseEntity {

    @TableField("reservation_id")
    private Long reservationId;

    @TableField("payment_method")
    private PaymentMethod paymentMethod;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @TableField("payment_status")
    private String paymentStatus;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("notes")
    private String notes;

    // 关联的预订信息（非数据库字段）
    @TableField(exist = false)
    private Reservation reservation;
}

