package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType> implements RoomTypeService {

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Override
    public RoomType findByTypeCode(String typeCode) {
        return roomTypeMapper.findByTypeCode(typeCode);
    }

    @Override
    public List<RoomType> findAllActive() {
        return roomTypeMapper.findAllActive();
    }

    @Override
    public boolean createRoomType(RoomType roomType) {
        // 检查房型代码是否已存在
        if (findByTypeCode(roomType.getTypeCode()) != null) {
            throw new RuntimeException("房型代码已存在");
        }

        return save(roomType);
    }
}

