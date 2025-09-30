-- 酒店管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotel_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE hotel_management;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role ENUM('ADMIN', 'STAFF') DEFAULT 'STAFF' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除'
) COMMENT='用户表';

-- 客户表
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('MALE', 'FEMALE') COMMENT '性别',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    id_card VARCHAR(18) UNIQUE COMMENT '身份证号',
    address VARCHAR(200) COMMENT '地址',
    birth_date DATE COMMENT '出生日期',
    nationality VARCHAR(50) DEFAULT '中国' COMMENT '国籍',
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除'
) COMMENT='客户表';

-- 房型表
CREATE TABLE IF NOT EXISTS room_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE COMMENT '房型名称',
    type_code VARCHAR(20) NOT NULL UNIQUE COMMENT '房型代码',
    description TEXT COMMENT '房型描述',
    base_price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    max_occupancy INT DEFAULT 2 COMMENT '最大入住人数',
    bed_type VARCHAR(50) COMMENT '床型',
    room_size DECIMAL(5,2) COMMENT '房间面积(平方米)',
    amenities TEXT COMMENT '设施描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除'
) COMMENT='房型表';

-- 房间表
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE COMMENT '房间号',
    room_type_id BIGINT NOT NULL COMMENT '房型ID',
    floor INT COMMENT '楼层',
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'OUT_OF_ORDER') DEFAULT 'AVAILABLE' COMMENT '房间状态',
    price DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    description TEXT COMMENT '房间描述',
    last_maintenance_date DATE COMMENT '最后维护日期',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    FOREIGN KEY (room_type_id) REFERENCES room_types(id)
) COMMENT='房间表';

-- 预订表
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    check_out_date DATE NOT NULL COMMENT '退房日期',
    adults_count INT DEFAULT 1 COMMENT '成人数量',
    children_count INT DEFAULT 0 COMMENT '儿童数量',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价格',
    deposit DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
    status ENUM('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED') DEFAULT 'PENDING' COMMENT '预订状态',
    special_requests TEXT COMMENT '特殊要求',
    reservation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预订时间',
    confirmation_time DATETIME COMMENT '确认时间',
    check_in_time DATETIME COMMENT '实际入住时间',
    check_out_time DATETIME COMMENT '实际退房时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
) COMMENT='预订表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL COMMENT '预订ID',
    payment_method ENUM('CASH', 'CARD', 'ALIPAY', 'WECHAT', 'BANK_TRANSFER') NOT NULL COMMENT '支付方式',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'SUCCESS' COMMENT '支付状态',
    transaction_id VARCHAR(100) COMMENT '交易流水号',
    notes TEXT COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) COMMENT='支付记录表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    details TEXT COMMENT '详细信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT='系统日志表';

-- 插入初始数据

-- 插入管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, real_name, role) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@hotel.com', '系统管理员', 'ADMIN');

-- 插入房型数据
INSERT INTO room_types (type_name, type_code, description, base_price, max_occupancy, bed_type, room_size, amenities) VALUES
('标准间', 'STD', '舒适的标准双人间，配备基础设施', 299.00, 2, '双床', 25.00, '空调,电视,WiFi,独立卫浴'),
('豪华间', 'DLX', '豪华装修的大床房，视野开阔', 499.00, 2, '大床', 35.00, '空调,液晶电视,WiFi,独立卫浴,迷你吧,保险箱'),
('套房', 'STE', '独立客厅的套房，适合商务客人', 999.00, 4, '大床+沙发床', 60.00, '空调,液晶电视,WiFi,独立卫浴,客厅,办公区,迷你吧,保险箱');

-- 插入房间数据
INSERT INTO rooms (room_number, room_type_id, floor, price) VALUES
('101', 1, 1, 299.00), ('102', 1, 1, 299.00), ('103', 1, 1, 299.00),
('201', 2, 2, 499.00), ('202', 2, 2, 499.00), ('203', 2, 2, 499.00),
('301', 3, 3, 999.00), ('302', 3, 3, 999.00);

-- 插入示例客户数据
INSERT INTO customers (name, gender, phone, email, id_card, address) VALUES 
('张三', 'MALE', '13800138000', 'zhangsan@example.com', '110101199001011234', '北京市朝阳区'),
('李四', 'FEMALE', '13800138001', 'lisi@example.com', '110101199002022345', '北京市海淀区'),
('王五', 'MALE', '13800138002', 'wangwu@example.com', '110101199003033456', '北京市西城区');