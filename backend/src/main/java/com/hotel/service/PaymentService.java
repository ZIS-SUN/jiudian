package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentService extends IService<Payment> {

    /**
     * 根据预订ID获取支付信息
     */
    Payment getByReservationId(Long reservationId);

    /**
     * 获取今日收入
     */
    BigDecimal getTodayRevenue();

    /**
     * 获取指定日期范围的收入
     */
    BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 处理退款
     */
    boolean processRefund(Long reservationId, BigDecimal refundAmount, String reason);

    /**
     * 创建支付记录
     */
    boolean createPayment(Payment payment);

    /**
     * 更新支付状态
     */
    boolean updatePaymentStatus(Long paymentId, String status);
}
