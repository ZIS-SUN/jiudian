# 酒店管理系统启动指南

## 环境要求
- Java 17+
- Node.js 16+  
- MySQL 8.0
- Maven 3.6+

## 1. 数据库初始化

### 启动MySQL服务
```bash
# Windows
net start mysql

# Linux/Mac
sudo systemctl start mysql
```

### 创建数据库
```bash
mysql -u root -p
```

```sql
source docs/database/init.sql
```

## 2. 后端启动

### 进入后端目录
```bash
cd backend
```

### 配置数据库连接
复制 `.env.example` 为 `.env` 并修改配置：
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=hotel_management
DB_USERNAME=root
DB_PASSWORD=你的密码
```

### 启动后端服务
```bash
mvn spring-boot:run
```

后端服务启动在：http://localhost:8080

## 3. 前端启动

### 进入前端目录
```bash
cd frontend
```

### 安装依赖
```bash
npm install
```

### 启动前端服务
```bash
npm run dev
```

前端服务启动在：http://localhost:5173

## 4. 访问系统

### 客户端首页
访问：http://localhost:5173

### 管理后台
访问：http://localhost:5173/admin

**默认管理员账号:**
- 用户名：admin
- 密码：admin123

## 5. API文档

后端API接口：
- 认证接口：`/api/auth/*`
- 客户管理：`/api/customers/*`
- 房间管理：`/api/rooms/*`
- 预订管理：`/api/reservations/*`
- 仪表盘：`/api/dashboard/*`

## 6. 项目结构

```
hotel-management/
├── backend/           # Spring Boot 后端
│   ├── src/main/java/com/hotel/
│   │   ├── controller/    # 控制器
│   │   ├── service/       # 业务逻辑
│   │   ├── mapper/        # 数据访问
│   │   ├── entity/        # 实体类
│   │   ├── enums/         # 枚举
│   │   ├── config/        # 配置
│   │   └── common/        # 通用工具
│   └── src/main/resources/
├── frontend/          # Vue3 前端
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 可复用组件
│   │   ├── api/           # API接口
│   │   ├── stores/        # Pinia状态管理
│   │   └── utils/         # 工具函数
└── docs/              # 项目文档
    ├── database/          # 数据库脚本
    └── SETUP.md          # 启动指南
```

## 7. 功能特性

### 已实现功能
✅ 用户认证与权限控制  
✅ 客房管理（增删改查）  
✅ 客户管理（增删改查）  
✅ 预订管理（预订流程管理）  
✅ 仪表盘数据统计  
✅ 响应式UI设计  

### 可扩展功能
🔄 房型管理  
🔄 支付管理  
🔄 房间预订日历  
🔄 客户端在线预订  
🔄 报表统计导出  

## 8. 常见问题

### 后端启动失败
1. 检查MySQL服务是否启动
2. 检查数据库连接配置
3. 确认Java版本17+

### 前端无法访问后端API
1. 检查后端服务是否正常启动
2. 检查CORS配置
3. 查看浏览器控制台错误信息

### 数据库连接失败
1. 确认MySQL用户权限
2. 检查数据库名称是否正确
3. 确认防火墙设置

## 9. 技术架构

**后端技术栈:**
- Spring Boot 3.2
- Spring Security + JWT
- MyBatis-Plus
- MySQL 8.0

**前端技术栈:**
- Vue 3 + Composition API
- Element-Plus UI组件库
- Pinia状态管理
- Axios网络请求
- Vite构建工具

## 10. 开发团队

感谢AI助手Claude Code的全程协助开发！

---

有问题请查看项目文档或联系开发团队。