package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 创建用户
     */
    boolean createUser(User user);

    /**
     * 更新用户密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}
