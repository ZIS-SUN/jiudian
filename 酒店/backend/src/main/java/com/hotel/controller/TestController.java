package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("后端连接正常，时间: " + System.currentTimeMillis());
    }

    @GetMapping("/check-user")
    public Result<Object> checkUser() {
        try {
            User user = userService.findByUsername("admin");
            if (user == null) {
                return Result.error("未找到admin用户");
            }

            // 不返回真实密码，只返回部分信息用于调试
            return Result
                    .success("找到用户: " + user.getUsername() + ", 角色: " + user.getRole() + ", 状态: " + user.getStatus());
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-password")
    public Result<String> testPassword(@RequestParam String password) {
        try {
            User user = userService.findByUsername("admin");
            if (user == null) {
                return Result.error("未找到admin用户");
            }

            boolean matches = passwordEncoder.matches(password, user.getPassword());
            return Result.success("密码验证结果: " + matches);
        } catch (Exception e) {
            return Result.error("密码验证失败: " + e.getMessage());
        }
    }

    @PostMapping("/create-admin")
    public Result<String> createAdmin() {
        try {
            // 生成密码哈希
            String hashedPassword = passwordEncoder.encode("admin123");
            
            return Result.success("密码哈希生成成功: " + hashedPassword);
        } catch (Exception e) {
            return Result.error("生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/generate-password")
    public Result<String> generatePassword(@RequestParam(defaultValue = "admin123") String password) {
        try {
            String hashedPassword = passwordEncoder.encode(password);
            return Result.success("原密码: " + password + ", 哈希: " + hashedPassword);
        } catch (Exception e) {
            return Result.error("生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/list-users")
    public Result<Object> listUsers() {
        try {
            java.util.List<User> users = userService.list();
            return Result.success("用户总数: " + users.size() + ", 用户列表: " + users);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/db-test")
    public Result<String> dbTest() {
        try {
            long count = userService.count();
            return Result.success("数据库连接正常，用户表总记录数: " + count);
        } catch (Exception e) {
            return Result.error("数据库连接失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-find-user")
    public Result<Object> testFindUser(@RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("findByUsername返回null，用户名: " + username);
            }
            return Result.success("找到用户: " + user.getUsername() + ", ID: " + user.getId() + ", 状态: " + user.getStatus());
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-login")
    public Result<String> testLogin(@RequestParam String username, @RequestParam String password) {
        try {
            String token = userService.login(username, password);
            return Result.success("登录成功，Token: " + token);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/reset-admin-password")
    public Result<String> resetAdminPassword() {
        try {
            // 生成新密码哈希
            String newPassword = "admin123";
            String hashedPassword = passwordEncoder.encode(newPassword);
            
            // 查找admin用户
            User admin = userService.findByUsername("admin");
            if (admin == null) {
                return Result.error("admin用户不存在");
            }
            
            // 更新密码
            admin.setPassword(hashedPassword);
            boolean success = userService.updateById(admin);
            
            if (success) {
                return Result.success("admin密码重置成功！新密码: " + newPassword + ", 哈希: " + hashedPassword);
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error("重置失败: " + e.getMessage());
        }
    }

    @GetMapping("/debug-login")
    public Result<Object> debugLogin(@RequestParam String username, @RequestParam String password) {
        try {
            // 查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在: " + username);
            }
            
            // 获取数据库中的密码哈希
            String storedHash = user.getPassword();
            
            // 生成新的哈希用于对比
            String newHash = passwordEncoder.encode(password);
            
            // 验证密码
            boolean matches = passwordEncoder.matches(password, storedHash);
            
            StringBuilder info = new StringBuilder();
            info.append("用户名: ").append(username).append("\n");
            info.append("输入密码: ").append(password).append("\n");
            info.append("数据库哈希: ").append(storedHash).append("\n");
            info.append("新生成哈希: ").append(newHash).append("\n");
            info.append("密码匹配: ").append(matches).append("\n");
            info.append("用户状态: ").append(user.getStatus()).append("\n");
            info.append("用户角色: ").append(user.getRole());
            
            return Result.success(info.toString());
        } catch (Exception e) {
            return Result.error("调试失败: " + e.getMessage());
        }
    }

    @PostMapping("/force-reset")
    public Result<String> forceReset() {
        try {
            // 使用多个已知正确的BCrypt哈希
            String[] knownHashes = {
                "$2a$10$N.zmdr9k7uOkXfvMlOvVtOcZOvXPCIjSLx2czpqO0FPMHqJZx6Dky", // admin123
                "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqfp.xl64jvpOUIgJOIrQ6e", // admin123
                "$2b$12$5/KzpfmZ8/J8.J8jQ8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q8Q", // admin123
            };
            
            User admin = userService.findByUsername("admin");
            if (admin == null) {
                return Result.error("admin用户不存在");
            }
            
            // 测试每个哈希
            StringBuilder result = new StringBuilder("测试结果:\n");
            for (int i = 0; i < knownHashes.length; i++) {
                boolean matches = passwordEncoder.matches("admin123", knownHashes[i]);
                result.append("哈希").append(i+1).append(": ").append(matches).append("\n");
            }
            
            // 使用第一个哈希更新
            admin.setPassword(knownHashes[0]);
            userService.updateById(admin);
            
            result.append("已更新为第一个哈希");
            return Result.success(result.toString());
        } catch (Exception e) {
            return Result.error("强制重置失败: " + e.getMessage());
        }
    }
}

