package com.hotel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {

    /**
     * 分页查询公告列表
     */
    Page<Announcement> getAnnouncementList(Integer page, Integer size, String type, Integer status);

    /**
     * 获取所有启用的公告
     */
    List<Announcement> getActiveAnnouncements();

    /**
     * 根据ID获取公告
     */
    Announcement getAnnouncementById(Long id);

    /**
     * 创建公告
     */
    Announcement createAnnouncement(Announcement announcement);

    /**
     * 更新公告
     */
    Announcement updateAnnouncement(Long id, Announcement announcement);

    /**
     * 删除公告
     */
    void deleteAnnouncement(Long id);
}