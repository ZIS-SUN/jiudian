# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hotel Management System (酒店管理系统) - A full-stack web application for hotel operations management with separate customer-facing and admin interfaces.

**Tech Stack:**
- Backend: Spring Boot 2.7.18 + MyBatis-Plus 3.5.4 + MySQL 8.0
- Frontend: Vue 3 + Element-Plus + Pinia + Vite
- Auth: Spring Security + JWT
- Java: 1.8 (backend uses Spring Boot 2.x, not 3.x despite README claim)

## Development Commands

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run          # Start backend server on port 8089
mvn clean install            # Build project
mvn test                     # Run tests
```

Backend runs at: `http://localhost:8089/api`

### Frontend (Vue 3)
```bash
cd frontend
npm install                  # Install dependencies
npm run dev                  # Start dev server on port 5173
npm run build               # Build for production
npm run preview             # Preview production build
```

Frontend runs at: `http://localhost:5173`

### Quick Start
Use the startup scripts in the root directory:
- Linux/Mac: `./start.sh`
- Windows: `start.bat`

These scripts start both backend and frontend in separate terminals.

## Database Setup

**Database:** MySQL 8.0 - `hotel_management`

Initialize database:
```bash
mysql -u root -p
source docs/database/init.sql
```

**Configuration:** `backend/src/main/resources/application.yml`
- Default DB credentials: `root/1234`
- Override with environment variables: `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`

## Architecture

### Backend Structure (`backend/src/main/java/com/hotel/`)

**Layered Architecture:**
1. **controller/** - REST API endpoints
   - `AuthController` - Login/register (`/auth/**`)
   - `RoomController` - Room management (`/rooms/**`)
   - `ReservationController` - Booking management (`/reservations/**`)
   - `CustomerController` - Customer management (`/customers/**`)
   - `OrderController` - Order management (`/orders/**`)
   - `DashboardController` - Statistics (`/dashboard/**`)

2. **service/** + **service/impl/** - Business logic layer
   - Service interfaces and implementations for each domain

3. **mapper/** - MyBatis-Plus data access layer
   - Extends `BaseMapper<T>` for CRUD operations

4. **entity/** - JPA entities
   - `BaseEntity` - Common fields (id, createTime, updateTime, deleted)
   - Domain entities extend BaseEntity

5. **config/** - Configuration classes
   - `SecurityConfig` - Spring Security with JWT filter chain
   - `JwtAuthenticationFilter` - Token validation filter
   - `JwtUtil` - Token generation/parsing
   - `CorsConfig` - CORS configuration
   - `MyBatisPlusConfig` - MyBatis-Plus pagination
   - `MetaObjectHandler` - Auto-fill timestamps

6. **dto/** - Data transfer objects for requests
7. **enums/** - Enum types (UserRole, RoomStatus, ReservationStatus, PaymentMethod, Gender)
8. **common/** - `Result<T>` - Standardized API response wrapper

**Security:**
- JWT-based authentication
- Role-based access control: `ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_USER`
- Admin endpoints (`/dashboard/**`, `/customers/**`, `/rooms/**`, `/reservations/**`, `/orders/**`) require `ADMIN` or `STAFF` roles
- Public endpoints: `/auth/**`, `/test/**`

### Frontend Structure (`frontend/src/`)

**Vue 3 Composition API + Pinia:**

1. **views/** - Page components
   - Public pages: `HomeModern.vue`, `Rooms.vue`, `Contact.vue`, `RoomDetail.vue`
   - Auth pages: `Login.vue`, `Register.vue`
   - User pages: `MyBookings.vue`, `Profile.vue`
   - Admin pages: `admin/Dashboard.vue`, `admin/Rooms.vue`, `admin/Reservations.vue`, `admin/Customers.vue`, `admin/Orders.vue`

2. **layouts/** - Layout wrappers
   - `AdminLayout.vue` - Admin panel layout with sidebar navigation

3. **components/** - Reusable Vue components

4. **api/** - API service modules
   - `index.js` - Axios instance with interceptors
   - Domain-specific API files: `auth.js`, `rooms.js`, `reservations.js`, `customers.js`, `orders.js`
   - Base URL: `http://localhost:8089/api`
   - Token sent via `Authorization: Bearer <token>` header

5. **stores/** - Pinia state management
   - `user.js` - User authentication state, role checking (`isAdmin`, `isUser`)

6. **router/** - Vue Router configuration
   - Route guards check `requiresAuth` meta field
   - Protected routes redirect to `/login` if no token

7. **data/** - Mock data (`mockData.js`)
   - Contains test users, room types, bookings for development

8. **utils/** - Utility functions

**Authentication Flow:**
- User logs in via `/auth/login` → Backend returns JWT token + user info
- Token stored in `localStorage`
- Axios interceptor attaches token to all requests
- Frontend shows/hides UI elements based on user role:
  - Not logged in: Show "Login" and "Register" links
  - Logged in (user): Show "My Bookings" link
  - Logged in (admin): Show "My Bookings" + "Admin Panel" links

**Test Accounts (from mock data):**
- Admin: `admin/admin123`
- User: `zhangsan/123456`

## Key Patterns

### Backend Patterns

**API Response Format:**
```java
Result.success(data)        // code: 200, data: T, message: "success"
Result.error(message)       // code: 500, data: null, message: string
```

**MyBatis-Plus:**
- Uses `LambdaQueryWrapper` for type-safe queries
- Logical delete enabled (`deleted` field: 0=active, 1=deleted)
- Auto-fill fields: `createTime`, `updateTime` via `MetaObjectHandler`

**Security:**
- JWT secret configurable via `JWT_SECRET` env var (default: `hotel-management-secret-key-2024`)
- Token expiration: 24 hours (86400000ms)
- Password encryption: BCrypt

### Frontend Patterns

**API Calls:**
```javascript
import api from '@/api/index'
const response = await api.post('/auth/login', credentials)
// Response structure: { code: 200, data: {...}, message: "..." }
```

**State Management:**
```javascript
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
userStore.login(credentials)
userStore.isAdmin  // computed property
```

**Router Guards:**
- Check `meta.requiresAuth` for protected routes
- Redirect to `/login` if not authenticated

## Important Notes

1. **Port Configuration:**
   - Backend: 8089 (configured in `application.yml`)
   - Frontend: 5173 (Vite default)
   - Backend context path: `/api`

2. **CORS:**
   - Backend allows `http://localhost:5173` via `CorsConfig`
   - Credentials allowed for cookie/auth headers

3. **MyBatis-Plus Logging:**
   - SQL logging enabled in dev: `log-impl: org.apache.ibatis.logging.stdout.StdOutImpl`
   - Application logs: `com.hotel: debug`

4. **Database Conventions:**
   - Table names: snake_case (e.g., `room_type`)
   - Column names: snake_case, auto-mapped to camelCase in Java
   - All entities have `id`, `create_time`, `update_time`, `deleted` fields

5. **Frontend Mock Data:**
   - System includes comprehensive mock data in `frontend/src/data/mockData.js`
   - Used for development when backend is unavailable
   - See `VIRTUAL_DATA_GUIDE.md` for mock data details

6. **Known Issues (see BUGFIX_SUMMARY.md):**
   - Permission control in navigation bar was fixed
   - Mock login fallback implemented to handle API errors
   - Role-based UI rendering implemented

## Common Tasks

### Adding a New Entity

1. Create entity class in `backend/src/main/java/com/hotel/entity/` extending `BaseEntity`
2. Create mapper interface in `mapper/` extending `BaseMapper<T>`
3. Create service interface in `service/` and implementation in `service/impl/`
4. Create controller in `controller/` with REST endpoints
5. Add security rules in `SecurityConfig.configure()` if needed
6. Create corresponding API module in `frontend/src/api/`
7. Create Vue views/components in `frontend/src/views/`

### Testing Authentication

Use the test endpoint: `GET /api/test/**` (no auth required)

### Running Single Test

```bash
cd backend
mvn test -Dtest=ClassName#methodName
```

### Debugging Backend

Check logs for:
- SQL queries (MyBatis-Plus prints all SQL)
- JWT token validation issues
- Spring Security filter chain
- CORS errors

### Debugging Frontend

- Check browser console for API errors
- Check Network tab for request/response details
- Verify token in localStorage: `localStorage.getItem('token')`
- Check Pinia store state in Vue DevTools