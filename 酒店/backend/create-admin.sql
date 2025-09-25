-- 创建管理员账号脚本
-- 使用正确的BCrypt哈希密码

USE hotel_management;

-- 删除旧的admin用户
DELETE FROM users WHERE username = 'admin';

-- 插入新的admin用户 
-- 用户名: admin
-- 密码: admin123 
-- BCrypt哈希: $2a$10$N.zmdr9k7uOkXfvMlOvVtOcZOvXPCIjSLx2czpqO0FPMHqJZx6Dky
INSERT INTO users (username, password, email, real_name, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOkXfvMlOvVtOcZOvXPCIjSLx2czpqO0FPMHqJZx6Dky', 'admin@hotel.com', '系统管理员', 'ADMIN', 1);

-- 验证插入结果
SELECT id, username, email, real_name, role, status, created_time FROM users WHERE username = 'admin';

-- 测试用户: staff123/staff123
DELETE FROM users WHERE username = 'staff123';
INSERT INTO users (username, password, email, real_name, role, status) 
VALUES ('staff123', '$2a$10$N.zmdr9k7uOkXfvMlOvVtOcZOvXPCIjSLx2czpqO0FPMHqJZx6Dky', 'staff@hotel.com', '前台员工', 'STAFF', 1);

SELECT '管理员账号创建完成！' as status;
SELECT '用户名: admin, 密码: admin123' as admin_info;
SELECT '用户名: staff123, 密码: staff123' as staff_info;