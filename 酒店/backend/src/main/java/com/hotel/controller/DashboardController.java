package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;
import com.hotel.enums.RoomStatus;
import com.hotel.service.CustomerService;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 房间统计
            Map<String, Long> roomStats = roomService.getRoomStatusStatistics();
            statistics.put("rooms", roomStats);

            // 预订统计
            Map<String, Object> reservationStats = new HashMap<>();
            reservationStats.put("total", reservationService.count());
            reservationStats.put("pending", reservationService.countByStatus(ReservationStatus.PENDING));
            reservationStats.put("confirmed", reservationService.countByStatus(ReservationStatus.CONFIRMED));
            reservationStats.put("checkedIn", reservationService.countByStatus(ReservationStatus.CHECKED_IN));
            reservationStats.put("today", reservationService.countByDate(LocalDate.now()));
            statistics.put("reservations", reservationStats);

            // 客户统计
            Map<String, Long> customerStats = new HashMap<>();
            customerStats.put("total", customerService.count());
            statistics.put("customers", customerStats);

            return Result.success(statistics);
        } catch (Exception e) {
            System.err.println("获取仪表盘统计失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/recent-reservations")
    public Result<List<Reservation>> getRecentReservations() {
        try {
            // 获取最近的预订记录（限制数量）
            List<Reservation> recentReservations = reservationService.findAllWithDetails()
                    .stream()
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());
            return Result.success(recentReservations);
        } catch (Exception e) {
            System.err.println("获取最近预订失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
