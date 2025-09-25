package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoomTypeMapper extends BaseMapper<RoomType> {
    
    @Select("SELECT * FROM room_types WHERE type_code = #{typeCode} AND deleted = 0")
    RoomType findByTypeCode(@Param("typeCode") String typeCode);
    
    @Select("SELECT * FROM room_types WHERE deleted = 0 ORDER BY created_time ASC")
    List<RoomType> findAllActive();
}



