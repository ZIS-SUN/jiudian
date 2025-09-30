package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Payment;
import com.hotel.mapper.PaymentMapper;
import com.hotel.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public Payment getByReservationId(Long reservationId) {
        try {
            return paymentMapper.getByReservationId(reservationId);
        } catch (Exception e) {
            log.error("根据预订ID获取支付信息失败: reservationId={}", reservationId, e);
            return null;
        }
    }

    @Override
    public BigDecimal getTodayRevenue() {
        try {
            LocalDate today = LocalDate.now();
            return paymentMapper.getRevenueByDate(today);
        } catch (Exception e) {
            log.error("获取今日收入失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return paymentMapper.getRevenueByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.error("获取指定日期范围收入失败: startDate={}, endDate={}", startDate, endDate, e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Transactional
    public boolean processRefund(Long reservationId, BigDecimal refundAmount, String reason) {
        try {
            Payment payment = getByReservationId(reservationId);
            if (payment == null) {
                log.warn("未找到预订的支付记录: reservationId={}", reservationId);
                return false;
            }

            // 创建退款记录
            Payment refundPayment = new Payment();
            refundPayment.setReservationId(reservationId);
            refundPayment.setAmount(refundAmount.negate()); // 负数表示退款
            refundPayment.setPaymentMethod(payment.getPaymentMethod());
            refundPayment.setPaymentTime(LocalDateTime.now());
            refundPayment.setPaymentStatus("REFUNDED");
            refundPayment.setNotes(reason);

            return save(refundPayment);
        } catch (Exception e) {
            log.error("处理退款失败: reservationId={}, refundAmount={}", reservationId, refundAmount, e);
            return false;
        }
    }

    @Override
    public boolean createPayment(Payment payment) {
        try {
            payment.setPaymentTime(LocalDateTime.now());
            return save(payment);
        } catch (Exception e) {
            log.error("创建支付记录失败", e);
            return false;
        }
    }

    @Override
    public boolean updatePaymentStatus(Long paymentId, String status) {
        try {
            Payment payment = getById(paymentId);
            if (payment != null) {
                payment.setPaymentStatus(status);
                return updateById(payment);
            }
            return false;
        } catch (Exception e) {
            log.error("更新支付状态失败: paymentId={}, status={}", paymentId, status, e);
            return false;
        }
    }
}
