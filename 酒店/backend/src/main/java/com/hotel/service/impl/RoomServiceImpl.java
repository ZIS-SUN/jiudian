package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Room;
import com.hotel.enums.RoomStatus;
import com.hotel.mapper.RoomMapper;
import com.hotel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public Room findByRoomNumber(String roomNumber) {
        return roomMapper.findByRoomNumber(roomNumber);
    }

    @Override
    public Long countByStatus(RoomStatus status) {
        return roomMapper.countByStatus(status);
    }

    @Override
    public Map<String, Long> getRoomStatusStatistics() {
        Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("available", countByStatus(RoomStatus.AVAILABLE));
        statistics.put("occupied", countByStatus(RoomStatus.OCCUPIED));
        statistics.put("maintenance", countByStatus(RoomStatus.MAINTENANCE));
        statistics.put("outOfOrder", countByStatus(RoomStatus.OUT_OF_ORDER));

        long total = statistics.values().stream().mapToLong(Long::longValue).sum();
        statistics.put("total", total);

        return statistics;
    }

    @Override
    public List<Room> findAllWithRoomType() {
        return roomMapper.findAllWithRoomType();
    }

    @Override
    public IPage<Room> pageWithCondition(Page<Room> page, String roomNumber, Long roomTypeId, RoomStatus status) {
        return roomMapper.selectPageWithCondition(page, roomNumber, roomTypeId, status);
    }

    @Override
    public boolean createRoom(Room room) {
        // 检查房间号是否已存在
        if (findByRoomNumber(room.getRoomNumber()) != null) {
            throw new RuntimeException("房间号已存在");
        }

        // 设置默认状态
        if (room.getStatus() == null) {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        return save(room);
    }

    @Override
    public boolean updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = getById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        room.setStatus(status);
        return updateById(room);
    }
}
