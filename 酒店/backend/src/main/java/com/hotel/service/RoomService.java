package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Room;
import com.hotel.enums.RoomStatus;

import java.util.List;
import java.util.Map;

public interface RoomService extends IService<Room> {

    /**
     * 根据房间号查找房间
     */
    Room findByRoomNumber(String roomNumber);

    /**
     * 根据状态统计房间数量
     */
    Long countByStatus(RoomStatus status);

    /**
     * 获取房间状态统计
     */
    Map<String, Long> getRoomStatusStatistics();

    /**
     * 获取所有房间（包含房型信息）
     */
    List<Room> findAllWithRoomType();

    /**
     * 分页查询房间（带条件）
     */
    IPage<Room> pageWithCondition(Page<Room> page, String roomNumber, Long roomTypeId, RoomStatus status);

    /**
     * 创建房间
     */
    boolean createRoom(Room room);

    /**
     * 更新房间状态
     */
    boolean updateRoomStatus(Long roomId, RoomStatus status);
}

