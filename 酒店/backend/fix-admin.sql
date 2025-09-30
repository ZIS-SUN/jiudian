-- 修复管理员账号脚本
USE hotel_management;

-- 先查看当前用户
SELECT * FROM users;

-- 删除所有admin用户
DELETE FROM users WHERE username = 'admin';

-- 重新插入管理员账号 (使用更简单的方式)
-- 密码: admin123 的BCrypt哈希 
INSERT INTO users (username, password, email, real_name, role, status, created_time, updated_time, deleted) 
VALUES (
    'admin', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqfp.xl64jvpOUIgJOIrQ6e', 
    'admin@hotel.com', 
    '系统管理员', 
    'ADMIN', 
    1, 
    NOW(), 
    NOW(), 
    0
);

-- 确认插入结果
SELECT id, username, email, real_name, role, status FROM users WHERE username = 'admin';

-- 测试其他简单用户
INSERT INTO users (username, password, email, real_name, role, status, created_time, updated_time, deleted) 
VALUES (
    'test', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqfp.xl64jvpOUIgJOIrQ6e', 
    'test@hotel.com', 
    '测试用户', 
    'STAFF', 
    1, 
    NOW(), 
    NOW(), 
    0
);

SELECT '=== 用户创建完成 ===' as result;
SELECT 'admin/admin123 - 管理员' as admin_account;
SELECT 'test/admin123 - 普通员工' as test_account;