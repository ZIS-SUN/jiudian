-- 酒店管理系统测试数据插入脚本
-- 对应前端 mockData.js 中的虚拟数据

USE hotel_management;

-- 清理旧数据（保留管理员）
-- 先删除有外键依赖的表数据
DELETE FROM reservations;
DELETE FROM payments;
DELETE FROM users WHERE username != 'admin';
DELETE FROM customers;

-- 插入管理员和工作人员用户（对应前端 admin 类型用户）
INSERT INTO users (username, password, email, real_name, role, status) VALUES
-- 管理员：admin / admin123（已存在，这里是备份）
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@hotel.com', 'Admin Zhang', 'ADMIN', 1),
-- 前台经理：manager / manager123
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'manager@hotel.com', 'Manager Li', 'STAFF', 1),
-- 前台接待员：receptionist / rec123
('receptionist', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'rec@hotel.com', 'Wang Xiaomei', 'STAFF', 1)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    real_name = VALUES(real_name),
    role = VALUES(role),
    status = VALUES(status);

-- 插入客户数据（对应前端普通用户）
INSERT INTO customers (name, gender, phone, email, id_card, address, birth_date, nationality) VALUES
('Zhang San', 'MALE', '13800138001', 'zhangsan@example.com', '110101199001011234', 'Beijing Chaoyang District', '1990-01-01', 'China'),
('Li Si', 'FEMALE', '13800138002', 'lisi@example.com', '110101199002022345', 'Shanghai Pudong District', '1992-02-02', 'China'),
('Wang Wu', 'MALE', '13800138003', 'wangwu@example.com', '110101199003033456', 'Guangzhou Tianhe District', '1985-03-03', 'China'),
('Zhao Liu', 'MALE', '13800138004', 'zhaoliu@example.com', '110101198804044567', 'Shenzhen Nanshan District', '1988-04-04', 'China'),
('Sun Qi', 'FEMALE', '13800138005', 'sunqi@example.com', '110101199505055678', 'Hangzhou Xihu District', '1995-05-05', 'China');

-- 创建一个用户凭证表，用于统一客户和员工的登录
CREATE TABLE IF NOT EXISTS user_credentials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    user_type ENUM('STAFF', 'CUSTOMER') NOT NULL COMMENT '用户类型',
    user_id BIGINT NOT NULL COMMENT '关联的用户或客户ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除'
) COMMENT='用户登录凭证表';

-- 清理旧的凭证数据
DELETE FROM user_credentials;

-- 插入员工登录凭证
INSERT INTO user_credentials (username, password, user_type, user_id, status)
SELECT username, password, 'STAFF', id, status
FROM users WHERE deleted = 0;

-- 插入客户登录凭证（使用简单密码 123456，实际应用中应该加密）
-- 注意：这里使用BCrypt加密的密码，对应明文 "123456"
INSERT INTO user_credentials (username, password, user_type, user_id, status) VALUES
('zhangsan', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER',
    (SELECT id FROM customers WHERE name = 'Zhang San'), 1),
('lisi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER',
    (SELECT id FROM customers WHERE name = 'Li Si'), 1),
('wangwu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER',
    (SELECT id FROM customers WHERE name = 'Wang Wu'), 1),
('zhaoliu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER',
    (SELECT id FROM customers WHERE name = 'Zhao Liu'), 1),
('sunqi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER',
    (SELECT id FROM customers WHERE name = 'Sun Qi'), 1);

-- 验证数据插入
SELECT '=== 员工用户 ===' as info;
SELECT id, username, real_name, role, status FROM users WHERE deleted = 0;

SELECT '=== 客户数据 ===' as info;
SELECT id, name, phone, email FROM customers WHERE deleted = 0;

SELECT '=== 登录凭证 ===' as info;
SELECT id, username, user_type, user_id, status FROM user_credentials WHERE deleted = 0;

-- 插入一些示例预订数据（对应前端 mockBookings）
INSERT INTO reservations (order_id, customer_id, room_id, check_in_date, check_out_date,
    adults_count, children_count, total_price, status, special_requests) VALUES
('HT20241201001',
    (SELECT id FROM customers WHERE name = 'Zhang San'),
    (SELECT id FROM rooms WHERE room_number = '201'),
    '2024-12-15', '2024-12-18', 2, 0, 1497.00, 'CONFIRMED', 'High floor room, non-smoking'),
('HT20241201002',
    (SELECT id FROM customers WHERE name = 'Li Si'),
    (SELECT id FROM rooms WHERE room_number = '301'),
    '2024-12-20', '2024-12-22', 2, 0, 1998.00, 'PENDING', 'Business facilities needed'),
('HT20241201003',
    (SELECT id FROM customers WHERE name = 'Zhao Liu'),
    (SELECT id FROM rooms WHERE room_number = '302'),
    '2024-12-10', '2024-12-12', 2, 0, 1998.00, 'CHECKED_OUT', 'Presidential suite, private butler service');

-- 验证预订数据
SELECT '=== 预订记录 ===' as info;
SELECT r.order_id, c.name as customer_name, rm.room_number, r.check_in_date, r.check_out_date, r.total_price, r.status
FROM reservations r
JOIN customers c ON r.customer_id = c.id
JOIN rooms rm ON r.room_id = rm.id
WHERE r.deleted = 0;