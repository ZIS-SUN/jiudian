-- 删除旧的admin用户
DELETE FROM users WHERE username = 'admin';

-- 插入新的admin用户 (密码: admin123)
INSERT INTO users (username, password, email, real_name, role, status) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@hotel.com', '系统管理员', 'ADMIN', 1);

-- 验证插入结果
SELECT id, username, email, real_name, role, status FROM users WHERE username = 'admin';