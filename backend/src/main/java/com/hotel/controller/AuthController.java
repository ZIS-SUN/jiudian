package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.LoginRequest;
import com.hotel.dto.RegisterRequest;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            User user = userService.findByUsername(loginRequest.getUsername());

            // 不返回密码
            user.setPassword(null);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            System.err.println("登录失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 验证密码确认
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                return Result.error("两次输入的密码不一致");
            }

            // 创建用户
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setEmail(registerRequest.getEmail());
            user.setPhone(registerRequest.getPhone());
            user.setRealName(registerRequest.getRealName());
            user.setRole(registerRequest.getRole());

            boolean success = userService.register(user);
            if (success) {
                // 注册成功后自动登录
                String token = userService.login(user.getUsername(), registerRequest.getPassword());
                user.setPassword(null); // 不返回密码

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);

                return Result.success("注册成功", data);
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        return Result.success("退出成功");
    }

    @GetMapping("/user")
    public Result<User> getCurrentUser(@RequestParam String username) {
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                user.setPassword(null); // 不返回密码
            }
            return Result.success(user);
        } catch (Exception e) {
            System.err.println("获取用户信息失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("后端连接正常");
    }
}
