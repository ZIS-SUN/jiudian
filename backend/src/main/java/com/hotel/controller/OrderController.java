package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Payment;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;
import com.hotel.service.PaymentService;
import com.hotel.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 获取订单列表（整合预订和支付信息）
     */
    @GetMapping
    public Result<IPage<Map<String, Object>>> getOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String customerInfo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String dateRange) {
        try {
            Page<Reservation> page = new Page<>(current, size);
            
            // 获取预订列表
            IPage<Reservation> reservationPage = reservationService.pageWithCondition(
                page, orderId, customerInfo, status != null ? ReservationStatus.valueOf(status.toUpperCase()) : null
            );
            
            // 构建订单数据（整合预订和支付信息）
            Page<Map<String, Object>> orderPage = new Page<>(current, size);
            orderPage.setTotal(reservationPage.getTotal());
            orderPage.setCurrent(reservationPage.getCurrent());
            orderPage.setSize(reservationPage.getSize());
            
            List<Map<String, Object>> orders = reservationPage.getRecords().stream()
                .map(this::buildOrderData)
                .collect(java.util.stream.Collectors.toList());
            
            orderPage.setRecords(orders);
            
            return Result.success(orderPage);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 订单状态统计
            statistics.put("pending", reservationService.countByStatus(ReservationStatus.PENDING));
            statistics.put("confirmed", reservationService.countByStatus(ReservationStatus.CONFIRMED));
            statistics.put("checkedIn", reservationService.countByStatus(ReservationStatus.CHECKED_IN));
            statistics.put("completed", reservationService.countByStatus(ReservationStatus.CHECKED_OUT));
            statistics.put("cancelled", reservationService.countByStatus(ReservationStatus.CANCELLED));
            
            // 今日收入
            BigDecimal todayRevenue = paymentService.getTodayRevenue();
            statistics.put("todayRevenue", todayRevenue != null ? todayRevenue : BigDecimal.ZERO);
            
            // 本月订单数
            statistics.put("monthlyOrders", reservationService.countByDateRange(
                LocalDate.now().withDayOfMonth(1), 
                LocalDate.now()
            ));
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取订单统计失败", e);
            return Result.error("获取订单统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderById(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.getByIdWithDetails(id);
            if (reservation == null) {
                return Result.error("订单不存在");
            }
            
            Map<String, Object> orderData = buildOrderData(reservation);
            return Result.success(orderData);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新订单状态
     */
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> orderIds = (List<Long>) request.get("orderIds");
            String status = (String) request.get("status");
            
            if (orderIds == null || orderIds.isEmpty()) {
                return Result.error("请选择要操作的订单");
            }
            
            ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
            
            for (Long orderId : orderIds) {
                reservationService.updateStatus(orderId, reservationStatus);
            }
            
            return Result.success("批量更新状态成功");
        } catch (Exception e) {
            log.error("批量更新订单状态失败", e);
            return Result.error("批量更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 退款处理
     */
    @PostMapping("/{id}/refund")
    public Result<Void> processRefund(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal refundAmount = new BigDecimal(request.get("refundAmount").toString());
            String reason = (String) request.get("reason");
            
            // 处理退款逻辑
            paymentService.processRefund(id, refundAmount, reason);
            
            // 更新预订状态为已取消
            reservationService.cancelReservation(id);
            
            return Result.success("退款处理成功");
        } catch (Exception e) {
            log.error("退款处理失败", e);
            return Result.error("退款处理失败: " + e.getMessage());
        }
    }

    /**
     * 构建订单数据（整合预订和支付信息）
     */
    private Map<String, Object> buildOrderData(Reservation reservation) {
        Map<String, Object> orderData = new HashMap<>();

        // 基本订单信息
        orderData.put("id", reservation.getId());
        orderData.put("orderId", reservation.getOrderId());
        orderData.put("type", "hotel"); // 酒店订单类型
        orderData.put("status", getOrderStatusText(reservation.getStatus()));
        orderData.put("totalPrice", reservation.getTotalPrice()); // 前端期望totalPrice
        orderData.put("totalAmount", reservation.getTotalPrice()); // 兼容totalAmount
        orderData.put("deposit", reservation.getDeposit());
        orderData.put("createdTime", reservation.getReservationTime()); // 前端期望createdTime
        orderData.put("createTime", reservation.getReservationTime()); // 兼容createTime
        
        // 客户信息
        if (reservation.getCustomer() != null) {
            Map<String, Object> customer = new HashMap<>();
            customer.put("id", reservation.getCustomer().getId());
            customer.put("name", reservation.getCustomer().getName());
            customer.put("phone", reservation.getCustomer().getPhone());
            customer.put("email", reservation.getCustomer().getEmail());
            orderData.put("customer", customer);
        }
        
        // 房间信息
        if (reservation.getRoom() != null) {
            Map<String, Object> room = new HashMap<>();
            room.put("id", reservation.getRoom().getId());
            room.put("roomNumber", reservation.getRoom().getRoomNumber());
            if (reservation.getRoom().getRoomType() != null) {
                room.put("typeName", reservation.getRoom().getRoomType().getTypeName());
                // 添加roomType对象供前端使用
                Map<String, Object> roomType = new HashMap<>();
                roomType.put("id", reservation.getRoom().getRoomType().getId());
                roomType.put("typeName", reservation.getRoom().getRoomType().getTypeName());
                room.put("roomType", roomType);
            } else {
                room.put("typeName", "");
            }
            orderData.put("room", room);
        }
        
        // 入住信息
        orderData.put("checkInDate", reservation.getCheckInDate());
        orderData.put("checkOutDate", reservation.getCheckOutDate());
        orderData.put("adultsCount", reservation.getAdultsCount());
        orderData.put("childrenCount", reservation.getChildrenCount());
        orderData.put("guestCount", reservation.getAdultsCount() + reservation.getChildrenCount()); // 添加总人数
        orderData.put("specialRequests", reservation.getSpecialRequests());
        orderData.put("notes", reservation.getSpecialRequests()); // 兼容notes字段
        
        // 支付信息
        try {
            Payment payment = paymentService.getByReservationId(reservation.getId());
            if (payment != null) {
                Map<String, Object> paymentInfo = new HashMap<>();
                paymentInfo.put("paymentMethod", payment.getPaymentMethod());
                paymentInfo.put("paymentStatus", payment.getPaymentStatus());
                paymentInfo.put("paymentTime", payment.getPaymentTime());
                paymentInfo.put("transactionId", payment.getTransactionId());
                orderData.put("payment", paymentInfo);
                // 在顶级也添加支付状态供前端直接使用
                orderData.put("paymentStatus", payment.getPaymentStatus());
                orderData.put("paymentMethod", payment.getPaymentMethod());
                orderData.put("transactionId", payment.getTransactionId());
            } else {
                orderData.put("payment", null);
                orderData.put("paymentStatus", "unpaid");
            }
        } catch (Exception e) {
            log.warn("获取支付信息失败: " + e.getMessage());
            orderData.put("payment", null);
            orderData.put("paymentStatus", "unpaid");
        }
        
        return orderData;
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(ReservationStatus status) {
        switch (status) {
            case PENDING:
                return "待确认";
            case CONFIRMED:
                return "已确认";
            case CHECKED_IN:
                return "已入住";
            case CHECKED_OUT:
                return "已完成";
            case CANCELLED:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 导出订单数据
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String customerInfo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String dateRange) {
        try {
            // 获取所有符合条件的订单数据
            Page<Reservation> page = new Page<>(1, Integer.MAX_VALUE); // 获取所有数据
            IPage<Reservation> reservationPage = reservationService.pageWithCondition(
                page, orderId, customerInfo, status != null ? ReservationStatus.valueOf(status.toUpperCase()) : null
            );

            // 构建CSV数据
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("订单号,客户姓名,客户电话,房间号,房型,入住日期,退房日期,订单状态,总金额,支付状态,创建时间\n");
            
            for (Reservation reservation : reservationPage.getRecords()) {
                Map<String, Object> orderData = buildOrderData(reservation);
                
                csvContent.append(String.format("\"%s\",", orderData.get("orderId")));
                csvContent.append(String.format("\"%s\",", getCustomerName(orderData)));
                csvContent.append(String.format("\"%s\",", getCustomerPhone(orderData)));
                csvContent.append(String.format("\"%s\",", getRoomNumber(orderData)));
                csvContent.append(String.format("\"%s\",", getRoomTypeName(orderData)));
                csvContent.append(String.format("\"%s\",", orderData.get("checkInDate")));
                csvContent.append(String.format("\"%s\",", orderData.get("checkOutDate")));
                csvContent.append(String.format("\"%s\",", orderData.get("status")));
                csvContent.append(String.format("\"%s\",", orderData.get("totalAmount")));
                csvContent.append(String.format("\"%s\",", getPaymentStatus(orderData)));
                csvContent.append(String.format("\"%s\"\n", formatDateTime(orderData.get("createTime"))));
            }

            byte[] csvBytes = csvContent.toString().getBytes(StandardCharsets.UTF_8);
            
            // 添加BOM以支持Excel正确显示中文
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] result = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, result, 0, bom.length);
            System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);

            String filename = "订单数据_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFilename);
            
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("导出订单失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 辅助方法
    private String getCustomerName(Map<String, Object> orderData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> customer = (Map<String, Object>) orderData.get("customer");
        return customer != null ? (String) customer.get("name") : "";
    }

    private String getCustomerPhone(Map<String, Object> orderData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> customer = (Map<String, Object>) orderData.get("customer");
        return customer != null ? (String) customer.get("phone") : "";
    }

    private String getRoomNumber(Map<String, Object> orderData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> room = (Map<String, Object>) orderData.get("room");
        return room != null ? (String) room.get("roomNumber") : "";
    }

    private String getRoomTypeName(Map<String, Object> orderData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> room = (Map<String, Object>) orderData.get("room");
        return room != null ? (String) room.get("typeName") : "";
    }

    private String getPaymentStatus(Map<String, Object> orderData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> payment = (Map<String, Object>) orderData.get("payment");
        return payment != null ? (String) payment.get("paymentStatus") : "未支付";
    }

    private String formatDateTime(Object dateTime) {
        if (dateTime instanceof LocalDateTime) {
            return ((LocalDateTime) dateTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return dateTime != null ? dateTime.toString() : "";
    }
}
