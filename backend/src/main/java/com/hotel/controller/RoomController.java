package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.enums.RoomStatus;
import com.hotel.service.RoomService;
import com.hotel.service.RoomTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping
    public Result<IPage<Room>> getRooms(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) RoomStatus status) {
        try {
            Page<Room> page = new Page<>(current, size);
            IPage<Room> result = roomService.pageWithCondition(page, roomNumber, roomTypeId, status);
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("获取房间列表失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<Map<String, Long>> getRoomStatistics() {
        try {
            Map<String, Long> statistics = roomService.getRoomStatusStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            System.err.println("获取房间统计失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/types")
    public Result<List<RoomType>> getRoomTypes() {
        try {
            List<RoomType> roomTypes = roomTypeService.findAllActive();
            return Result.success(roomTypes);
        } catch (Exception e) {
            System.err.println("获取房型列表失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Room> getRoomById(@PathVariable Long id) {
        try {
            Room room = roomService.getById(id);
            return Result.success(room);
        } catch (Exception e) {
            System.err.println("获取房间详情失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Room> createRoom(@RequestBody Room room) {
        try {
            roomService.createRoom(room);
            return Result.success("创建房间成功", room);
        } catch (Exception e) {
            System.err.println("创建房间失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        try {
            room.setId(id);
            roomService.updateById(room);
            return Result.success("更新房间成功", room);
        } catch (Exception e) {
            System.err.println("更新房间失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateRoomStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            RoomStatus status = RoomStatus.valueOf(statusRequest.get("status"));
            roomService.updateRoomStatus(id, status);
            return Result.success("更新房间状态成功");
        } catch (Exception e) {
            System.err.println("更新房间状态失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        try {
            roomService.removeById(id);
            return Result.success("删除房间成功");
        } catch (Exception e) {
            System.err.println("删除房间失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
