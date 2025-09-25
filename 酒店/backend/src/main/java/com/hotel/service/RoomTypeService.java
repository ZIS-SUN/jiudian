package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.RoomType;

import java.util.List;

public interface RoomTypeService extends IService<RoomType> {
    
    /**
     * 根据房型代码查找房型
     */
    RoomType findByTypeCode(String typeCode);
    
    /**
     * 获取所有启用的房型
     */
    List<RoomType> findAllActive();
    
    /**
     * 创建房型
     */
    boolean createRoomType(RoomType roomType);
}



