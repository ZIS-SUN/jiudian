package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Room;
import com.hotel.enums.RoomStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    
    @Select("SELECT * FROM rooms WHERE room_number = #{roomNumber} AND deleted = 0")
    Room findByRoomNumber(@Param("roomNumber") String roomNumber);
    
    @Select("SELECT COUNT(*) FROM rooms WHERE status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") RoomStatus status);
    
    @Select({
        "SELECT r.*, rt.type_name, rt.type_code, rt.description as type_description ",
        "FROM rooms r ",
        "LEFT JOIN room_types rt ON r.room_type_id = rt.id ",
        "WHERE r.deleted = 0 AND rt.deleted = 0 ",
        "ORDER BY r.room_number ASC"
    })
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "room_number", property = "roomNumber"),
        @Result(column = "room_type_id", property = "roomTypeId"),
        @Result(column = "type_name", property = "roomType.typeName"),
        @Result(column = "type_code", property = "roomType.typeCode"),
        @Result(column = "type_description", property = "roomType.description")
    })
    List<Room> findAllWithRoomType();
    
    @Select({
        "<script>",
        "SELECT r.*, rt.type_name, rt.type_code ",
        "FROM rooms r ",
        "LEFT JOIN room_types rt ON r.room_type_id = rt.id ",
        "WHERE r.deleted = 0 AND rt.deleted = 0",
        "<if test='roomNumber != null and roomNumber != \"\"'>",
        "  AND r.room_number LIKE CONCAT('%', #{roomNumber}, '%')",
        "</if>",
        "<if test='roomTypeId != null'>",
        "  AND r.room_type_id = #{roomTypeId}",
        "</if>",
        "<if test='status != null'>",
        "  AND r.status = #{status}",
        "</if>",
        "ORDER BY r.room_number ASC",
        "</script>"
    })
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "room_number", property = "roomNumber"),
        @Result(column = "room_type_id", property = "roomTypeId"),
        @Result(column = "type_name", property = "roomType.typeName"),
        @Result(column = "type_code", property = "roomType.typeCode")
    })
    IPage<Room> selectPageWithCondition(Page<Room> page,
                                      @Param("roomNumber") String roomNumber,
                                      @Param("roomTypeId") Long roomTypeId,
                                      @Param("status") RoomStatus status);
}



