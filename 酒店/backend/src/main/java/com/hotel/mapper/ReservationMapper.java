package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Reservation;
import com.hotel.enums.ReservationStatus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    @Select("SELECT * FROM reservations WHERE order_id = #{orderId} AND deleted = 0")
    Reservation findByOrderId(@Param("orderId") String orderId);
    
    @Select("SELECT COUNT(*) FROM reservations WHERE status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") ReservationStatus status);
    
    @Select("SELECT COUNT(*) FROM reservations WHERE DATE(created_time) = #{date} AND deleted = 0")
    Long countByDate(@Param("date") LocalDate date);
    
    @Select({
        "SELECT r.*, c.name as customer_name, c.phone as customer_phone, ",
        "rm.room_number, rt.type_name as room_type_name ",
        "FROM reservations r ",
        "LEFT JOIN customers c ON r.customer_id = c.id ",
        "LEFT JOIN rooms rm ON r.room_id = rm.id ",
        "LEFT JOIN room_types rt ON rm.room_type_id = rt.id ",
        "WHERE r.deleted = 0 AND c.deleted = 0 AND rm.deleted = 0 ",
        "ORDER BY r.created_time DESC"
    })
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "order_id", property = "orderId"),
        @Result(column = "customer_id", property = "customerId"),
        @Result(column = "room_id", property = "roomId"),
        @Result(column = "customer_name", property = "customer.name"),
        @Result(column = "customer_phone", property = "customer.phone"),
        @Result(column = "room_number", property = "room.roomNumber"),
        @Result(column = "room_type_name", property = "room.roomType.typeName")
    })
    List<Reservation> findAllWithDetails();
    
    @Select({
        "<script>",
        "SELECT r.*, c.name as customer_name, c.phone as customer_phone, ",
        "rm.room_number, rt.type_name as room_type_name ",
        "FROM reservations r ",
        "LEFT JOIN customers c ON r.customer_id = c.id ",
        "LEFT JOIN rooms rm ON r.room_id = rm.id ",
        "LEFT JOIN room_types rt ON rm.room_type_id = rt.id ",
        "WHERE r.deleted = 0 AND c.deleted = 0 AND rm.deleted = 0",
        "<if test='orderId != null and orderId != \"\"'>",
        "  AND r.order_id LIKE CONCAT('%', #{orderId}, '%')",
        "</if>",
        "<if test='customerName != null and customerName != \"\"'>",
        "  AND c.name LIKE CONCAT('%', #{customerName}, '%')",
        "</if>",
        "<if test='status != null'>",
        "  AND r.status = #{status}",
        "</if>",
        "ORDER BY r.created_time DESC",
        "</script>"
    })
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "order_id", property = "orderId"),
        @Result(column = "customer_id", property = "customerId"),
        @Result(column = "room_id", property = "roomId"),
        @Result(column = "customer_name", property = "customer.name"),
        @Result(column = "customer_phone", property = "customer.phone"),
        @Result(column = "room_number", property = "room.roomNumber"),
        @Result(column = "room_type_name", property = "room.roomType.typeName")
    })
    IPage<Reservation> selectPageWithCondition(Page<Reservation> page,
                                             @Param("orderId") String orderId,
                                             @Param("customerName") String customerName,
                                             @Param("status") ReservationStatus status);

    @Select({
        "SELECT r.*, c.name as customer_name, c.phone as customer_phone, c.email as customer_email, ",
        "rm.room_number, rt.type_name as room_type_name ",
        "FROM reservations r ",
        "LEFT JOIN customers c ON r.customer_id = c.id ",
        "LEFT JOIN rooms rm ON r.room_id = rm.id ",
        "LEFT JOIN room_types rt ON rm.room_type_id = rt.id ",
        "WHERE r.id = #{id} AND r.deleted = 0"
    })
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "order_id", property = "orderId"),
        @Result(column = "customer_id", property = "customerId"),
        @Result(column = "room_id", property = "roomId"),
        @Result(column = "customer_name", property = "customer.name"),
        @Result(column = "customer_phone", property = "customer.phone"),
        @Result(column = "customer_email", property = "customer.email"),
        @Result(column = "room_number", property = "room.roomNumber"),
        @Result(column = "room_type_name", property = "room.typeName")
    })
    Reservation getByIdWithDetails(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM reservations WHERE DATE(created_time) BETWEEN #{startDate} AND #{endDate} AND deleted = 0")
    Long countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}



