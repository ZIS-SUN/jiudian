-- 修复管理员密码脚本
USE hotel_management;

-- 方案1: 使用在线BCrypt工具生成的哈希 (admin123)
UPDATE users 
SET password = '$2a$12$4GqZFWngI/7n.8SCGo0s6OQZSjTOYtmFq4Lq6.iVB7rLfpHDsaQNe'
WHERE username = 'admin';

-- 验证更新结果
SELECT username, password, status FROM users WHERE username = 'admin';

-- 备用方案：简单密码 123456
INSERT INTO users (username, password, email, real_name, role, status, created_time, updated_time, deleted) 
VALUES (
    'test123', 
    '$2a$12$3Ub3WO7Ol4I4Y6Xz4A4rJerJfQFxQpO3N4zL7rFqhKSGJpb.8P8CK', 
    'test@hotel.com', 
    '测试管理员', 
    'ADMIN', 
    1, 
    NOW(), 
    NOW(), 
    0
) ON DUPLICATE KEY UPDATE password = VALUES(password);

-- 显示所有管理员账号
SELECT username, role, status, '对应密码' as password_hint FROM users WHERE role = 'ADMIN';
SELECT 'admin 对应密码: admin123' as hint1;
SELECT 'test123 对应密码: 123456' as hint2;