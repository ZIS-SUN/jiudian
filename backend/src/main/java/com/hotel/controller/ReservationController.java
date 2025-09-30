package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;
import com.hotel.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<IPage<Reservation>> getReservations(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) ReservationStatus status) {
        try {
            Page<Reservation> page = new Page<>(current, size);
            IPage<Reservation> result = reservationService.pageWithCondition(page, orderId, customerName, status);
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("获取预订列表失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getReservationStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("pending", reservationService.countByStatus(ReservationStatus.PENDING));
            statistics.put("confirmed", reservationService.countByStatus(ReservationStatus.CONFIRMED));
            statistics.put("checkedIn", reservationService.countByStatus(ReservationStatus.CHECKED_IN));
            statistics.put("checkedOut", reservationService.countByStatus(ReservationStatus.CHECKED_OUT));
            statistics.put("cancelled", reservationService.countByStatus(ReservationStatus.CANCELLED));
            statistics.put("today", reservationService.countByDate(LocalDate.now()));

            return Result.success(statistics);
        } catch (Exception e) {
            System.err.println("获取预订统计失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Reservation> getReservationById(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.getById(id);
            return Result.success(reservation);
        } catch (Exception e) {
            System.err.println("获取预订详情失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            reservationService.createReservation(reservation);
            return Result.success("创建预订成功", reservation);
        } catch (Exception e) {
            System.err.println("创建预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        try {
            reservation.setId(id);
            reservationService.updateById(reservation);
            return Result.success("更新预订成功", reservation);
        } catch (Exception e) {
            System.err.println("更新预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirmReservation(@PathVariable Long id) {
        try {
            reservationService.confirmReservation(id);
            return Result.success("确认预订成功");
        } catch (Exception e) {
            System.err.println("确认预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/checkin")
    public Result<Void> checkIn(@PathVariable Long id) {
        try {
            reservationService.checkIn(id);
            return Result.success("办理入住成功");
        } catch (Exception e) {
            System.err.println("办理入住失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/checkout")
    public Result<Void> checkOut(@PathVariable Long id) {
        try {
            reservationService.checkOut(id);
            return Result.success("办理退房成功");
        } catch (Exception e) {
            System.err.println("办理退房失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return Result.success("取消预订成功");
        } catch (Exception e) {
            System.err.println("取消预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.removeById(id);
            return Result.success("删除预订成功");
        } catch (Exception e) {
            System.err.println("删除预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
