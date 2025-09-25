# 数据库配置说明

## 环境要求
- MySQL 8.0+
- 默认用户：root
- 默认密码：1234

## 数据库初始化

### 1. 创建数据库
```sql
mysql -u root -p
source init.sql
```

### 2. 验证数据库
```sql
USE hotel_management;
SHOW TABLES;
```

### 3. 检查初始数据
```sql
-- 检查用户表
SELECT * FROM users;

-- 检查房型数据
SELECT * FROM room_types;

-- 检查房间数据
SELECT * FROM rooms;
```

## 环境变量配置

### 后端配置
复制 `.env.example` 为 `.env` 并修改以下配置：

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=hotel_management
DB_USERNAME=root
DB_PASSWORD=1234
```

### 安全建议
1. 生产环境请修改默认密码
2. 使用专门的数据库用户，不要使用root
3. 定期备份数据库
4. 启用MySQL的慢查询日志

## 数据库表结构说明

### 核心表
- `users`: 系统用户表（管理员、员工）
- `customers`: 客户信息表
- `room_types`: 房型表
- `rooms`: 房间表
- `reservations`: 预订表
- `payments`: 支付记录表
- `system_logs`: 系统日志表

### 默认管理员账号
- 用户名: admin
- 密码: admin123

## 常用SQL命令

### 重置数据库
```sql
DROP DATABASE IF EXISTS hotel_management;
source init.sql
```

### 清空测试数据
```sql
-- 清空预订数据（保留基础数据）
DELETE FROM payments WHERE id > 0;
DELETE FROM reservations WHERE id > 0;
```