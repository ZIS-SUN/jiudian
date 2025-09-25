package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    
    @Select("SELECT * FROM payments WHERE reservation_id = #{reservationId} AND deleted = 0")
    List<Payment> findByReservationId(@Param("reservationId") Long reservationId);
    
    @Select("SELECT * FROM payments WHERE transaction_id = #{transactionId} AND deleted = 0")
    Payment findByTransactionId(@Param("transactionId") String transactionId);
    
    @Select("SELECT * FROM payments WHERE reservation_id = #{reservationId} AND deleted = 0 LIMIT 1")
    Payment getByReservationId(@Param("reservationId") Long reservationId);
    
    @Select("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(payment_time) = #{date} AND payment_status = 'PAID' AND deleted = 0")
    BigDecimal getRevenueByDate(@Param("date") LocalDate date);
    
    @Select("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(payment_time) BETWEEN #{startDate} AND #{endDate} AND payment_status = 'PAID' AND deleted = 0")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}



