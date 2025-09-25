package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService extends IService<Reservation> {

    /**
     * 根据订单号查找预订
     */
    Reservation findByOrderId(String orderId);

    /**
     * 根据状态统计预订数量
     */
    Long countByStatus(ReservationStatus status);

    /**
     * 统计指定日期的预订数量
     */
    Long countByDate(LocalDate date);

    /**
     * 获取所有预订（包含详细信息）
     */
    List<Reservation> findAllWithDetails();

    /**
     * 分页查询预订（带条件）
     */
    IPage<Reservation> pageWithCondition(Page<Reservation> page, String orderId, String customerName,
            ReservationStatus status);

    /**
     * 创建预订
     */
    boolean createReservation(Reservation reservation);

    /**
     * 确认预订
     */
    boolean confirmReservation(Long reservationId);

    /**
     * 办理入住
     */
    boolean checkIn(Long reservationId);

    /**
     * 办理退房
     */
    boolean checkOut(Long reservationId);

    /**
     * 取消预订
     */
    boolean cancelReservation(Long reservationId);

    /**
     * 生成订单号
     */
    String generateOrderId();

    /**
     * 根据ID获取预订详情（包含关联信息）
     */
    Reservation getByIdWithDetails(Long id);

    /**
     * 统计指定日期范围的预订数量
     */
    Long countByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 更新预订状态
     */
    boolean updateStatus(Long reservationId, ReservationStatus status);
}

