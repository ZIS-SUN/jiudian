package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Announcement;
import com.hotel.mapper.AnnouncementMapper;
import com.hotel.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Page<Announcement> getAnnouncementList(Integer page, Integer size, String type, Integer status) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();

        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(Announcement::getType, type);
        }

        if (status != null) {
            queryWrapper.eq(Announcement::getStatus, status);
        }

        queryWrapper.orderByDesc(Announcement::getPriority)
                   .orderByDesc(Announcement::getCreatedTime);

        return announcementMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)
                   .orderByDesc(Announcement::getPriority)
                   .orderByDesc(Announcement::getCreatedTime);

        return announcementMapper.selectList(queryWrapper);
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        announcement.setId(id);
        announcementMapper.updateById(announcement);
        return announcementMapper.selectById(id);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }
}