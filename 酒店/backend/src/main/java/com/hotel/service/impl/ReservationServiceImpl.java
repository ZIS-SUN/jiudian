package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;
import com.hotel.enums.RoomStatus;
import com.hotel.mapper.ReservationMapper;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private RoomService roomService;

    @Override
    public Reservation findByOrderId(String orderId) {
        return reservationMapper.findByOrderId(orderId);
    }

    @Override
    public Long countByStatus(ReservationStatus status) {
        return reservationMapper.countByStatus(status);
    }

    @Override
    public Long countByDate(LocalDate date) {
        return reservationMapper.countByDate(date);
    }

    @Override
    public List<Reservation> findAllWithDetails() {
        return reservationMapper.findAllWithDetails();
    }

    @Override
    public IPage<Reservation> pageWithCondition(Page<Reservation> page, String orderId, String customerName,
            ReservationStatus status) {
        return reservationMapper.selectPageWithCondition(page, orderId, customerName, status);
    }

    @Override
    @Transactional
    public boolean createReservation(Reservation reservation) {
        // 生成订单号
        reservation.setOrderId(generateOrderId());

        // 设置预订时间和状态
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);

        return save(reservation);
    }

    @Override
    @Transactional
    public boolean confirmReservation(Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预订不存在");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("预订状态不正确，无法确认");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmationTime(LocalDateTime.now());

        return updateById(reservation);
    }

    @Override
    @Transactional
    public boolean checkIn(Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预订不存在");
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("预订状态不正确，无法办理入住");
        }

        // 更新预订状态
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInTime(LocalDateTime.now());

        // 更新房间状态为已入住
        roomService.updateRoomStatus(reservation.getRoomId(), RoomStatus.OCCUPIED);

        return updateById(reservation);
    }

    @Override
    @Transactional
    public boolean checkOut(Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预订不存在");
        }

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new RuntimeException("预订状态不正确，无法办理退房");
        }

        // 更新预订状态
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckOutTime(LocalDateTime.now());

        // 更新房间状态为可用
        roomService.updateRoomStatus(reservation.getRoomId(), RoomStatus.AVAILABLE);

        return updateById(reservation);
    }

    @Override
    public boolean cancelReservation(Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预订不存在");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN ||
                reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new RuntimeException("预订状态不正确，无法取消");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        // 如果房间已被占用，需要释放
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            roomService.updateRoomStatus(reservation.getRoomId(), RoomStatus.AVAILABLE);
        }

        return updateById(reservation);
    }

    @Override
    public String generateOrderId() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = count() + 1;
        return "R" + dateStr + String.format("%04d", count);
    }

    @Override
    public Reservation getByIdWithDetails(Long id) {
        try {
            return reservationMapper.getByIdWithDetails(id);
        } catch (Exception e) {
            log.error("根据ID获取预订详情失败: id={}", id, e);
            return null;
        }
    }

    @Override
    public Long countByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return reservationMapper.countByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.error("统计日期范围预订数量失败: startDate={}, endDate={}", startDate, endDate, e);
            return 0L;
        }
    }

    @Override
    public boolean updateStatus(Long reservationId, ReservationStatus status) {
        try {
            Reservation reservation = getById(reservationId);
            if (reservation != null) {
                reservation.setStatus(status);
                return updateById(reservation);
            }
            return false;
        } catch (Exception e) {
            log.error("更新预订状态失败: reservationId={}, status={}", reservationId, status, e);
            return false;
        }
    }
}
