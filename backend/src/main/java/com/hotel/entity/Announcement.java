package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("announcements")
public class Announcement extends BaseEntity {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型：NOTICE-通知, PROMOTION-优惠, MAINTENANCE-维护, URGENT-紧急
     */
    private String type;

    /**
     * 优先级：1-高, 2-中, 3-低
     */
    private Integer priority;

    /**
     * 状态：1-启用, 0-禁用
     */
    private Integer status;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 发布者姓名
     */
    private String publisherName;
}