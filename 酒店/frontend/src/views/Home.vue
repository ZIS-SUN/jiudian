<template>
  <div class="hotel-home">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <div class="logo-icon">
            <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="40" height="40" rx="8" fill="url(#gradient)"/>
              <path d="M8 24h24v8H8v-8z" fill="white" opacity="0.9"/>
              <path d="M12 20h16v4H12v-4z" fill="white" opacity="0.7"/>
              <path d="M16 16h8v4h-8v-4z" fill="white" opacity="0.5"/>
              <circle cx="20" cy="12" r="2" fill="white"/>
              <defs>
                <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#667eea"/>
                  <stop offset="100%" style="stop-color:#764ba2"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div class="logo-text">
            <h2>盛世国际酒店</h2>
            <span>尊贵体验 · 无与伦比</span>
          </div>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>

          <!-- 登录后才显示的链接 -->
          <router-link v-if="isLoggedIn" to="/my-bookings" class="nav-link">我的预订</router-link>
          <router-link v-if="isLoggedIn" to="/profile" class="nav-link">个人中心</router-link>

          <!-- 未登录时显示登录注册 -->
          <template v-if="!isLoggedIn">
            <router-link to="/login" class="nav-link user-login">用户登录</router-link>
            <router-link to="/register" class="nav-link user-register">立即注册</router-link>
          </template>

          <!-- 登录后显示用户信息和登出 -->
          <template v-else>
            <span class="user-info">{{ userInfo?.realName || userInfo?.name || userInfo?.username }}</span>
            <button class="logout-btn" @click="handleLogout">退出登录</button>
          </template>

          <!-- 管理员专用链接 -->
          <router-link v-if="isAdmin" to="/admin" class="nav-link admin-link">管理后台</router-link>
        </div>
      </div>
    </nav>

    <!-- 英雄区域 -->
    <section class="hero">
      <div class="hero-background">
        <div class="hero-overlay"></div>
        <div class="hero-particles"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">
          <span>国际五星级酒店</span>
        </div>
        <h1 class="hero-title">
          <span class="title-line1">盛世国际酒店</span>
          <span class="title-line2">GRANDEUR INTERNATIONAL HOTEL</span>
        </h1>
        <p class="hero-subtitle">
          传承世纪精英传统，融合现代豪华设计<br/>
          为您呈现无可挑剔的尺贵体验
        </p>
        <div class="hero-actions">
          <router-link to="/rooms" class="btn btn-primary">
            <span>立即预订</span>
            <i class="btn-icon">→</i>
          </router-link>
          <a href="#about" class="btn btn-outline">
            <span>探索更多</span>
          </a>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">150+</div>
            <div class="stat-label">豪华客房</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">24/7</div>
            <div class="stat-label">精品服务</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">5★</div>
            <div class="stat-label">国际标准</div>
          </div>
        </div>
      </div>

      <!-- 快速预订卡片 -->
      <div class="quick-booking">
        <div class="booking-header">
          <h3>即时预订</h3>
          <p>专业预订服务，为您提供最优价格</p>
        </div>
        <div class="booking-form">
          <div class="form-group">
            <label>
              <i class="form-icon">📅</i>
              入住日期
            </label>
            <el-date-picker
              v-model="checkInDate"
              type="date"
              placeholder="选择入住日期"
              :disabled-date="disabledDate"
              size="large"
            />
          </div>
          <div class="form-group">
            <label>
              <i class="form-icon">📅</i>
              退房日期
            </label>
            <el-date-picker
              v-model="checkOutDate"
              type="date"
              placeholder="选择退房日期"
              :disabled-date="disabledDate"
              size="large"
            />
          </div>
          <div class="form-group">
            <label>
              <i class="form-icon">🏨</i>
              客房数量
            </label>
            <el-select
              v-model="roomCount"
              placeholder="选择房间数"
              size="large"
            >
              <el-option label="1间客房" :value="1" />
              <el-option label="2间客房" :value="2" />
              <el-option label="3间客房" :value="3" />
              <el-option label="4间客房" :value="4" />
            </el-select>
          </div>
          <div class="form-group">
            <el-button
              type="primary"
              size="large"
              class="search-btn"
              @click="searchRooms"
            >
              <i class="btn-search-icon">🔍</i>
              搜索客房
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 关于我们 -->
    <section id="about" class="about">
      <div class="container">
        <div class="section-header">
          <h2>关于我们</h2>
          <p>打造您的完美住宿体验</p>
        </div>
        <div class="about-grid">
          <div class="about-item">
            <div class="about-icon">🛏️</div>
            <h3>舒适客房</h3>
            <p>精心设计的客房，配备现代化设施，为您提供宾至如归的舒适体验。</p>
          </div>
          <div class="about-item">
            <div class="about-icon">🍽️</div>
            <h3>美食餐厅</h3>
            <p>多样化的餐饮选择，从本地特色到国际美食，满足您的味蕾需求。</p>
          </div>
          <div class="about-item">
            <div class="about-icon">🎯</div>
            <h3>贴心服务</h3>
            <p>24小时礼宾服务，专业团队为您提供个性化的服务体验。</p>
          </div>
          <div class="about-item">
            <div class="about-icon">📍</div>
            <h3>优越位置</h3>
            <p>位于城市中心，交通便利，周边商圈繁华，出行购物都便捷。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 房型推荐 -->
    <section class="room-showcase">
      <div class="container">
        <div class="section-header">
          <h2>精选房型</h2>
          <p>为您推荐最受欢迎的房型</p>
        </div>
        <div class="rooms-grid">
          <div class="room-card" v-for="room in featuredRooms" :key="room.id">
            <div class="room-image">
              <img :src="room.image" :alt="room.name" />
              <div class="room-badge">热门</div>
            </div>
            <div class="room-info">
              <h3>{{ room.name }}</h3>
              <p class="room-desc">{{ room.description }}</p>
              <div class="room-features">
                <span
                  class="feature"
                  v-for="feature in room.features"
                  :key="feature"
                >
                  {{ feature }}
                </span>
              </div>
              <div class="room-footer">
                <div class="room-price">
                  <span class="price">¥{{ room.price }}</span>
                  <span class="unit">/晚</span>
                </div>
                <el-button type="primary" @click="bookRoom(room)"
                  >立即预订</el-button
                >
              </div>
            </div>
          </div>
        </div>
        <div class="text-center" style="margin-top: 40px">
          <router-link to="/rooms" class="btn btn-outline"
            >查看所有房型</router-link
          >
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>🏨 星级酒店</h3>
            <p>为您提供舒适优质的住宿体验</p>
            <div class="social-links">
              <a href="#" class="social-link">📧</a>
              <a href="#" class="social-link">📱</a>
              <a href="#" class="social-link">🌐</a>
            </div>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <p>📍 地址：XX市XX区XX路XX号</p>
            <p>📞 电话：400-XXX-XXXX</p>
            <p>📧 邮箱：info@hotel.com</p>
          </div>
          <div class="footer-section">
            <h4>服务时间</h4>
            <p>前台服务：24小时</p>
            <p>餐厅服务：6:00 - 23:00</p>
            <p>客房服务：24小时</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 星级酒店. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

// 用户状态 - 使用计算属性保持响应性
const isLoggedIn = computed(() => userStore.isLoggedIn);
const userInfo = computed(() => userStore.userInfo);
const isAdmin = computed(() => userStore.isAdmin);

// 登出处理
const handleLogout = () => {
  userStore.logout();
  ElMessage.success('已退出登录');
};

// 预订表单数据
const checkInDate = ref("");
const checkOutDate = ref("");
const roomCount = ref(1);

// 推荐房型数据
const featuredRooms = ref([
  {
    id: 1,
    name: "皇家套房",
    description: "尊享的皇家体验，融合东方美学与现代豪华，独立阳台可俱瞰城市全景",
    price: 1888,
    image:
      "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600&h=400&fit=crop&crop=center",
    features: ["私人管家", "米其林餐厅", "私人温泉", "全景阳台"],
  },
  {
    id: 2,
    name: "商务精英套房",
    description: "为精英商务人士精心打造，独立办公区域、会议室及专属礼宾服务",
    price: 1388,
    image:
      "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=600&h=400&fit=crop&crop=center",
    features: ["专属办公室", "会议设施", "行政酒廊", "高级早餐"],
  },
  {
    id: 3,
    name: "家庭亲子套房",
    description: "为全家出游精心设计，宽敞舒适的空间配备安全儿童设施及亲子游戏区",
    price: 1188,
    image:
      "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=600&h=400&fit=crop&crop=center",
    features: ["亲子双床", "儿童游戏区", "安全防护", "家庭娱乐"],
  },
]);

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

// 搜索房间
const searchRooms = () => {
  if (!checkInDate.value || !checkOutDate.value) {
    ElMessage.warning("请选择入住和退房日期");
    return;
  }

  if (checkInDate.value >= checkOutDate.value) {
    ElMessage.warning("退房日期必须晚于入住日期");
    return;
  }

  // 跳转到房间列表页面，并传递搜索参数
  router.push({
    path: "/rooms",
    query: {
      checkIn: checkInDate.value,
      checkOut: checkOutDate.value,
      rooms: roomCount.value,
    },
  });
};

// 预订房间
const bookRoom = (room) => {
  router.push({
    path: "/booking",
    query: {
      roomId: room.id,
      roomType: room.name,
      price: room.price,
    },
  });
};

// 预览房间
const previewRoom = (room) => {
  router.push({
    path: "/room-detail",
    query: {
      id: room.id
    }
  });
};

onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo();

  // 设置默认入住日期为明天
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  checkInDate.value = tomorrow;

  // 设置默认退房日期为后天
  const dayAfter = new Date();
  dayAfter.setDate(dayAfter.getDate() + 2);
  checkOutDate.value = dayAfter;
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.hotel-home {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  color: #1a1a1a;
  background: #fafafa;
  overflow-x: hidden;
}

/* 导航栏 - Apple Design */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 1000;
  padding: 1rem 0;
  box-shadow: var(--shadow-sm);
  border-bottom: 1px solid var(--color-border);
  transition: all var(--transition-base);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
}

.logo {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text h2 {
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 1.375rem;
  letter-spacing: -0.01em;
  margin: 0;
}

.logo-text span {
  color: var(--color-text-light);
  font-size: 0.75rem;
  font-weight: 400;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.nav-links {
  display: flex;
  gap: 2.5rem;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  color: var(--color-text-primary);
  font-weight: 500;
  font-size: 0.9375rem;
  transition: all var(--transition-fast);
  position: relative;
  padding: 0.5rem 0;
  opacity: 0.8;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: var(--color-primary);
  transition: width var(--transition-fast);
  border-radius: 1px;
}

.nav-link:hover {
  color: var(--color-primary);
  opacity: 1;
}

.nav-link:hover::after {
  width: 20px;
}

.admin-link {
  background: var(--color-text-primary) !important;
  color: var(--color-bg-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  transition: all var(--transition-fast) !important;
  border: none !important;
  opacity: 1 !important;
}

.admin-link::after {
  display: none !important;
}

.admin-link:hover {
  background: var(--color-text-secondary) !important;
  color: var(--color-bg-primary) !important;
}

.user-login {
  background: var(--color-primary) !important;
  color: white !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  transition: all var(--transition-fast) !important;
  border: none !important;
  opacity: 1 !important;
}

.user-login::after {
  display: none !important;
}

.user-login:hover {
  background: var(--color-primary-hover) !important;
  color: white !important;
}

.user-register {
  background: transparent !important;
  color: var(--color-primary) !important;
  border: 1px solid var(--color-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  transition: all var(--transition-fast) !important;
  opacity: 1 !important;
}

.user-register::after {
  display: none !important;
}

.user-register:hover {
  background: var(--color-primary) !important;
  color: white !important;
}

/* 用户信息样式 */
.user-info {
  color: var(--color-text-primary) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  padding: 0.5rem 0 !important;
  opacity: 1 !important;
}

/* 登出按钮样式 */
.logout-btn {
  background: transparent !important;
  color: var(--color-danger) !important;
  border: 1px solid var(--color-danger) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  cursor: pointer !important;
  transition: all var(--transition-fast) !important;
  opacity: 1 !important;
}

.logout-btn:hover {
  background: var(--color-danger) !important;
  color: white !important;
}

/* 英雄区域 */
.hero {
  position: relative;
  min-height: 85vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
  padding: 6rem 0 4rem;
  background: var(--color-bg-secondary);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1) 0%, rgba(0, 122, 255, 0.05) 100%),
    url("https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=1920&h=1080&fit=crop&crop=center")
      center/cover;
  z-index: -2;
  opacity: 0.4;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
  z-index: -1;
}

.hero-particles {
  display: none;
}

.hero-content {
  max-width: 1000px;
  padding: 1rem 2rem;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.hero-badge {
  display: inline-block;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  padding: 0.5rem 1.25rem;
  margin-bottom: 2rem;
  font-size: 0.75rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: var(--color-text-secondary);
  animation: fadeInUp 0.8s ease-out;
}

.hero-title {
  margin-bottom: 1rem;
  animation: fadeInUp 1s ease-out 0.4s both;
}

.title-line1 {
  display: block;
  font-size: 3.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 0.5rem;
  color: var(--color-text-primary);
  line-height: 1.1;
  animation: fadeInUp 0.8s ease-out 0.1s both;
}

.title-line2 {
  display: block;
  font-size: 1rem;
  font-weight: 400;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--color-text-light);
  margin-bottom: 1.5rem;
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.hero-subtitle {
  font-size: 1.25rem;
  line-height: 1.6;
  margin-bottom: 2.5rem;
  color: var(--color-text-secondary);
  font-weight: 400;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  animation: fadeInUp 0.8s ease-out 0.3s both;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin-top: 3rem;
  animation: fadeInUp 0.8s ease-out 0.5s both;
}

.stat-item {
  text-align: center;
  transition: all var(--transition-fast);
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 2.25rem;
  font-weight: 700;
  display: block;
  margin-bottom: 0.25rem;
  color: var(--color-primary);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-text-light);
  font-weight: 500;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  animation: fadeInUp 1s ease-out 0.8s both;
  margin-bottom: 1.2rem;
}

.btn {
  padding: 0.875rem 2rem;
  text-decoration: none;
  border-radius: var(--radius-xl);
  font-weight: 500;
  font-size: 0.9375rem;
  transition: all var(--transition-fast);
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  position: relative;
  white-space: nowrap;
}


.btn-primary {
  background: var(--color-primary);
  color: white;
  box-shadow: var(--shadow-md);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
  box-shadow: var(--shadow-lg);
  transform: translateY(-1px);
}

.btn-outline {
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-text-primary);
  transition: all var(--transition-fast);
}

.btn-outline:hover {
  background-color: var(--color-text-primary);
  border-color: var(--color-text-primary);
  color: white;
}

.btn-icon {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.btn:hover .btn-icon {
  transform: translateX(3px);
}

/* 快速预订卡片 */
.quick-booking {
  position: relative;
  margin: 4rem auto 0;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 2.5rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border);
  width: 95%;
  max-width: 1000px;
  z-index: 10;
}

.booking-header {
  text-align: center;
  margin-bottom: 1.2rem;
}

.booking-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 0.3rem;
}

.booking-header p {
  color: #666;
  font-size: 0.9rem;
  font-weight: 400;
}

.booking-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  align-items: end;
}

.form-group {
  position: relative;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #1a1a1a;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-icon {
  font-size: 1rem;
  opacity: 0.7;
}

.search-btn {
  width: 100%;
  height: 44px;
  background: var(--color-primary) !important;
  border: none !important;
  border-radius: var(--radius-md) !important;
  font-weight: 500 !important;
  font-size: 0.9375rem !important;
  transition: all var(--transition-fast) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 0.5rem !important;
}

.search-btn:hover {
  background: var(--color-primary-hover) !important;
  transform: translateY(-1px) !important;
}

.btn-search-icon {
  font-size: 1.1rem;
}

/* 通用样式 */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.section-badge {
  display: inline-block;
  background: linear-gradient(135deg, #3498DB, #2980B9);
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 50px;
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 2.5rem;
  color: var(--color-text-primary);
  margin-bottom: 1rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.section-header p {
  font-size: 1.125rem;
  color: var(--color-text-secondary);
  line-height: 1.6;
  font-weight: 400;
  max-width: 600px;
  margin: 0 auto;
}

/* 关于我们 */
.about {
  padding: 6rem 0;
  background: var(--color-bg-primary);
  position: relative;
}

.about::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(52, 152, 219, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(44, 62, 80, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2.5rem;
  position: relative;
  z-index: 1;
}

.about-item {
  text-align: center;
  padding: 2.5rem 2rem;
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.about-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #3498DB, #2C3E50);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.about-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.about-item:hover::before {
  transform: scaleX(1);
}

.about-icon {
  margin-bottom: 2rem;
  display: flex;
  justify-content: center;
}

.about-item h3 {
  color: var(--color-text-primary);
  margin-bottom: 1rem;
  font-size: 1.375rem;
  font-weight: 600;
}

.feature-highlight {
  display: inline-block;
  background: linear-gradient(135deg, #3498DB, #2980B9);
  color: white;
  padding: 0.3rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 1rem;
}

.about-item p {
  color: var(--color-text-secondary);
  line-height: 1.6;
  font-size: 0.9375rem;
}

/* 房型推荐 */
.room-showcase {
  padding: 6rem 0;
  background: var(--color-bg-secondary);
  position: relative;
}

.room-showcase::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(52, 152, 219, 0.03) 0%, transparent 50%),
              radial-gradient(circle at 20% 80%, rgba(44, 62, 80, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 3rem;
  position: relative;
  z-index: 1;
}

.room-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);
  position: relative;
}

.room-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.room-image {
  position: relative;
  height: 280px;
  overflow: hidden;
}

.room-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.room-card:hover .room-image img {
  transform: scale(1.08);
}

.room-badge {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: var(--color-danger);
  color: white;
  padding: 0.375rem 0.875rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.room-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.8), rgba(44, 62, 80, 0.8));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.room-card:hover .room-overlay {
  opacity: 1;
}

.room-preview-btn {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.room-preview-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

.room-info {
  padding: 2rem;
}

.room-info h3 {
  color: var(--color-text-primary);
  margin-bottom: 0.75rem;
  font-size: 1.375rem;
  font-weight: 600;
}

.room-desc {
  color: var(--color-text-secondary);
  margin-bottom: 1.5rem;
  line-height: 1.6;
  font-size: 0.9375rem;
}

.room-features {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 2rem;
}

.feature {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  padding: 0.375rem 0.875rem;
  border-radius: var(--radius-xl);
  font-size: 0.8125rem;
  font-weight: 500;
  border: none;
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
}

.room-price {
  flex: 1;
}

.price-main {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
  margin-bottom: 0.25rem;
}

.currency {
  font-size: 1.2rem;
  color: #3498DB;
  font-weight: 600;
}

.price {
  font-size: 1.75rem;
  color: var(--color-text-primary);
  font-weight: 600;
}

.unit {
  color: #666;
  font-size: 1rem;
  font-weight: 500;
}

.price-note {
  color: #999;
  font-size: 0.8rem;
}

.book-btn {
  background: linear-gradient(135deg, #3498DB, #2980B9);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 6px 20px rgba(52, 152, 219, 0.3);
}

.book-btn:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 10px 30px rgba(52, 152, 219, 0.4);
}

.btn-arrow {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.book-btn:hover .btn-arrow {
  transform: translateX(3px);
}

/* 页脚 */
.footer {
  background: var(--color-text-primary);
  color: white;
  padding: 3rem 0 2rem;
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(52, 152, 219, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(231, 76, 60, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-section h3,
.footer-section h4 {
  margin-bottom: 1rem;
  color: white;
  font-weight: 600;
}

.social-links {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  text-decoration: none;
  transition: all var(--transition-fast);
}

.social-link:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid #333;
  color: #999;
}

.text-center {
  text-align: center;
}



/* 性能优化 */
* {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.hero-background,
.about::before,
.room-showcase::before,
.footer::before {
  will-change: transform;
}

.room-card,
.about-item,
.btn {
  will-change: transform;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    max-width: 1200px;
    padding: 0 1.5rem;
  }
  
  .title-line1 {
    font-size: 3.5rem;
  }
  
  .hero-stats {
    gap: 3rem;
  }
}

@media (max-width: 992px) {
  .nav-container {
    padding: 0 1.5rem;
  }
  
  .logo-text h2 {
    font-size: 1.3rem;
  }
  
  .nav-links {
    gap: 2rem;
  }
  
  .title-line1 {
    font-size: 3rem;
  }
  
  .hero-stats {
    gap: 2rem;
  }
  
  .stat-number {
    font-size: 2rem;
  }
  
  .about-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
  }
  
  .rooms-grid {
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 1rem 0;
  }
  
  .nav-links {
    display: none;
  }
  
  .logo {
    gap: 0.75rem;
  }
  
  .logo-text h2 {
    font-size: 1.2rem;
  }
  
  .logo-text span {
    font-size: 0.75rem;
  }
  
  .hero-content {
    padding: 1rem;
  }
  
  .title-line1 {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
    margin-bottom: 2rem;
  }
  
  .hero-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
    margin-top: 2rem;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 15px;
    padding: 1.5rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
  
  .stat-item {
    padding: 1rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    border: 1px solid rgba(255, 255, 255, 0.15);
  }
  
  .booking-form {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .quick-booking {
    width: 95%;
    padding: 1.5rem;
    bottom: -40px;
  }
  
  .booking-header h3 {
    font-size: 1.5rem;
  }
  
  .section-header h2 {
    font-size: 2.2rem;
  }
  
  .about {
    padding: 6rem 0 4rem;
  }
  
  .about-grid,
  .rooms-grid {
    grid-template-columns: 1fr;
  }
  
  .about-item {
    padding: 2rem 1.5rem;
  }
  
  .room-showcase {
    padding: 6rem 0;
  }
  
  .room-info {
    padding: 1.5rem;
  }
  
  .room-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .book-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }
  
  .title-line1 {
    font-size: 2rem;
    line-height: 1.1;
  }
  
  .hero-actions {
    flex-direction: column;
    gap: 1rem;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
  
  .quick-booking {
    padding: 1rem;
  }
  
  .search-btn {
    height: 44px !important;
  }
  
  .section-header h2 {
    font-size: 1.8rem;
  }
  
  .about-item {
    padding: 1.5rem 1rem;
  }
  
  .about-item h3 {
    font-size: 1.3rem;
  }
  
  .room-info h3 {
    font-size: 1.3rem;
  }
  
  .price {
    font-size: 1.8rem;
  }
}
</style>
