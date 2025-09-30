package com.hotel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Announcement;
import com.hotel.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/announcements")
@CrossOrigin
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 分页查询公告列表
     */
    @GetMapping
    public Result<Page<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {

        Page<Announcement> announcementPage = announcementService.getAnnouncementList(page, size, type, status);
        return Result.success(announcementPage);
    }

    /**
     * 获取所有启用的公告（公共接口）
     */
    @GetMapping("/active")
    public Result<List<Announcement>> getActiveAnnouncements() {
        List<Announcement> announcements = announcementService.getActiveAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 根据ID获取公告
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement != null) {
            return Result.success(announcement);
        }
        return Result.error("公告不存在");
    }

    /**
     * 创建公告
     */
    @PostMapping
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        Announcement created = announcementService.createAnnouncement(announcement);
        return Result.success(created);
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<Announcement> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody Announcement announcement) {

        Announcement updated = announcementService.updateAnnouncement(id, announcement);
        return Result.success(updated);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success(null);
    }
}