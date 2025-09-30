<template>
  <div class="rooms-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h2>🏨 星级酒店</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link active">房间预订</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>

          <!-- 登录后才显示的链接 -->
          <router-link v-if="isLoggedIn" to="/my-bookings" class="nav-link">我的预订</router-link>

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

    <div class="page-content">
      <!-- 搜索过滤器 -->
      <section class="search-section">
        <div class="container">
          <h1>选择您的理想客房</h1>
          <div class="search-filters">
            <div class="filter-group">
              <label>入住日期</label>
              <el-date-picker
                v-model="searchForm.checkInDate"
                type="date"
                placeholder="选择入住日期"
                :disabled-date="disabledDate"
                size="large"
                @change="handleSearch"
              />
            </div>
            <div class="filter-group">
              <label>退房日期</label>
              <el-date-picker
                v-model="searchForm.checkOutDate"
                type="date"
                placeholder="选择退房日期"
                :disabled-date="disabledDate"
                size="large"
                @change="handleSearch"
              />
            </div>
            <div class="filter-group">
              <label>房间数量</label>
              <el-select
                v-model="searchForm.roomCount"
                placeholder="选择房间数"
                size="large"
                @change="handleSearch"
              >
                <el-option label="1间客房" :value="1" />
                <el-option label="2间客房" :value="2" />
                <el-option label="3间客房" :value="3" />
                <el-option label="4间客房" :value="4" />
              </el-select>
            </div>
            <div class="filter-group">
              <label>价格范围</label>
              <el-select
                v-model="searchForm.priceRange"
                placeholder="选择价格范围"
                size="large"
                @change="handleSearch"
              >
                <el-option label="不限" value="" />
                <el-option label="300元以下" value="0-300" />
                <el-option label="300-600元" value="300-600" />
                <el-option label="600-1000元" value="600-1000" />
                <el-option label="1000元以上" value="1000-9999" />
              </el-select>
            </div>
            <div class="filter-group">
              <label>房型</label>
              <el-select
                v-model="searchForm.roomType"
                placeholder="选择房型"
                size="large"
                @change="handleSearch"
              >
                <el-option label="全部房型" value="" />
                <el-option label="标准房" value="standard" />
                <el-option label="豪华房" value="deluxe" />
                <el-option label="套房" value="suite" />
                <el-option label="家庭房" value="family" />
              </el-select>
            </div>
          </div>
        </div>
      </section>

      <!-- 房间列表 -->
      <section class="rooms-section">
        <div class="container">
          <div class="rooms-header">
            <div class="results-info">
              <span>找到 {{ filteredRooms.length }} 间符合条件的客房</span>
            </div>
            <div class="sort-controls">
              <el-select
                v-model="sortBy"
                placeholder="排序方式"
                size="default"
                @change="handleSort"
              >
                <el-option label="价格从低到高" value="price-asc" />
                <el-option label="价格从高到低" value="price-desc" />
                <el-option label="推荐度" value="rating" />
                <el-option label="房间面积" value="size" />
              </el-select>
            </div>
          </div>

          <div class="rooms-grid" v-loading="loading">
            <div
              class="room-item"
              v-for="room in paginatedRooms"
              :key="room.id"
            >
              <div class="room-gallery">
                <img
                  :src="room.images[0]"
                  :alt="room.name"
                  class="main-image"
                />
                <div class="image-indicator">
                  <span>1/{{ room.images.length }}</span>
                </div>
                <div class="room-badges">
                  <span class="badge hot" v-if="room.isHot">热门</span>
                  <span class="badge discount" v-if="room.discount"
                    >{{ room.discount }}折</span
                  >
                </div>
              </div>

              <div class="room-details">
                <div class="room-header">
                  <h3>{{ room.name }}</h3>
                  <div class="room-rating">
                    <span class="stars">★★★★★</span>
                    <span class="rating-score">{{ room.rating }}</span>
                  </div>
                </div>

                <p class="room-description">{{ room.description }}</p>

                <div class="room-specs">
                  <div class="spec-item">
                    <span class="spec-label">面积</span>
                    <span class="spec-value">{{ room.size }}m²</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-label">床型</span>
                    <span class="spec-value">{{ room.bedType }}</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-label">可住</span>
                    <span class="spec-value">{{ room.maxGuests }}人</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-label">楼层</span>
                    <span class="spec-value">{{ room.floor }}层</span>
                  </div>
                </div>

                <div class="room-amenities">
                  <span
                    class="amenity"
                    v-for="amenity in room.amenities"
                    :key="amenity"
                  >
                    {{ amenity }}
                  </span>
                </div>

                <div class="room-footer">
                  <div class="price-section">
                    <div
                      class="original-price"
                      v-if="room.originalPrice > room.price"
                    >
                      ¥{{ room.originalPrice }}
                    </div>
                    <div class="current-price">
                      <span class="price">¥{{ room.price }}</span>
                      <span class="unit">/晚</span>
                    </div>
                    <div class="total-price" v-if="getTotalNights() > 0">
                      共{{ getTotalNights() }}晚，总计：¥{{
                        (room.price * getTotalNights()).toLocaleString()
                      }}
                    </div>
                  </div>

                  <div class="action-buttons">
                    <el-button size="default" @click="viewRoomDetails(room)"
                      >查看详情</el-button
                    >
                    <el-button
                      type="primary"
                      size="default"
                      @click="bookRoom(room)"
                      :disabled="!room.available"
                    >
                      {{ room.available ? "立即预订" : "已满房" }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div
            class="pagination-wrapper"
            v-if="filteredRooms.length > pageSize"
          >
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="filteredRooms.length"
              layout="prev, pager, next, jumper"
              @current-change="handlePageChange"
            />
          </div>

          <!-- 无结果提示 -->
          <div class="no-results" v-if="!loading && filteredRooms.length === 0">
            <div class="no-results-icon">🔍</div>
            <h3>未找到符合条件的客房</h3>
            <p>请调整搜索条件或联系我们获取更多帮助</p>
            <el-button type="primary" @click="resetFilters">重置筛选</el-button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { mockRoomTypes } from "@/data/mockData";

const router = useRouter();
const route = useRoute();
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

// 数据状态
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(6);
const sortBy = ref("rating");

// 搜索表单
const searchForm = ref({
  checkInDate: "",
  checkOutDate: "",
  roomCount: 1,
  priceRange: "",
  roomType: "",
});

// 使用虚拟数据，转换格式适配现有组件
const allRooms = ref(mockRoomTypes.map(room => ({
  id: room.id,
  name: room.name,
  description: room.description,
  price: room.price,
  originalPrice: room.price > 500 ? Math.round(room.price * 1.3) : null,
  images: room.images.map(img => img.replace('w=800&h=600', 'w=600&h=400')),
  amenities: room.amenities.slice(0, 6),
  available: room.availableRooms > 0,
  size: room.area,
  bedType: room.bedType,
  maxGuests: room.capacity,
  floor: room.floorRange.split('-')[0].replace('楼', ''),
  rating: room.rating,
  isHot: room.rating >= 4.8 || room.price <= 400,
  discount: room.price > 500 ? (Math.random() * 2 + 7).toFixed(1) : null,
  type: room.price > 2000 ? 'suite' : room.price > 800 ? 'deluxe' : room.capacity > 2 ? 'family' : 'standard',
})));

// 过滤后的房间
const filteredRooms = computed(() => {
  let rooms = [...allRooms.value];

  // 价格过滤
  if (searchForm.value.priceRange) {
    const [min, max] = searchForm.value.priceRange.split("-").map(Number);
    rooms = rooms.filter((room) => room.price >= min && room.price <= max);
  }

  // 房型过滤
  if (searchForm.value.roomType) {
    rooms = rooms.filter((room) => room.type === searchForm.value.roomType);
  }

  // 排序
  switch (sortBy.value) {
    case "price-asc":
      rooms.sort((a, b) => a.price - b.price);
      break;
    case "price-desc":
      rooms.sort((a, b) => b.price - a.price);
      break;
    case "rating":
      rooms.sort((a, b) => b.rating - a.rating);
      break;
    case "size":
      rooms.sort((a, b) => b.size - a.size);
      break;
  }

  return rooms;
});

// 分页后的房间
const paginatedRooms = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredRooms.value.slice(start, end);
});

// 计算总晚数
const getTotalNights = () => {
  if (!searchForm.value.checkInDate || !searchForm.value.checkOutDate) return 0;
  const checkIn = new Date(searchForm.value.checkInDate);
  const checkOut = new Date(searchForm.value.checkOutDate);
  const diffTime = checkOut.getTime() - checkIn.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays > 0 ? diffDays : 0;
};

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  // 这里可以调用后端API
};

// 处理排序
const handleSort = () => {
  currentPage.value = 1;
};

// 处理分页
const handlePageChange = (page) => {
  currentPage.value = page;
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 重置筛选
const resetFilters = () => {
  searchForm.value = {
    checkInDate: "",
    checkOutDate: "",
    roomCount: 1,
    priceRange: "",
    roomType: "",
  };
  sortBy.value = "rating";
  currentPage.value = 1;
};

// 查看房间详情
const viewRoomDetails = (room) => {
  router.push({
    path: "/room-detail",
    query: { id: room.id },
  });
};

// 预订房间
const bookRoom = (room) => {
  if (!searchForm.value.checkInDate || !searchForm.value.checkOutDate) {
    ElMessage.warning("请先选择入住和退房日期");
    return;
  }

  router.push({
    path: "/booking",
    query: {
      roomId: room.id,
      roomName: room.name,
      price: room.price,
      checkIn: searchForm.value.checkInDate,
      checkOut: searchForm.value.checkOutDate,
      rooms: searchForm.value.roomCount,
    },
  });
};

// 初始化
onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo();

  // 从路由参数中获取搜索条件
  if (route.query.checkIn) {
    searchForm.value.checkInDate = route.query.checkIn;
  }
  if (route.query.checkOut) {
    searchForm.value.checkOutDate = route.query.checkOut;
  }
  if (route.query.rooms) {
    searchForm.value.roomCount = Number(route.query.rooms);
  }

  // 设置默认日期（如果没有传入）
  if (!searchForm.value.checkInDate) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    searchForm.value.checkInDate = tomorrow.toISOString().split("T")[0];
  }

  if (!searchForm.value.checkOutDate) {
    const dayAfter = new Date();
    dayAfter.setDate(dayAfter.getDate() + 2);
    searchForm.value.checkOutDate = dayAfter.toISOString().split("T")[0];
  }

  // 模拟加载
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 1000);
});
</script>

<style scoped>
.rooms-page {
  min-height: 100vh;
  background: var(--color-bg-secondary);
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
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
}

.logo h2 {
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 1.375rem;
  letter-spacing: -0.01em;
}

.nav-links {
  display: flex;
  gap: 2rem;
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

.nav-link:hover,
.nav-link.active {
  color: var(--color-primary);
  opacity: 1;
}

.admin-link {
  background: var(--color-text-primary) !important;
  color: var(--color-bg-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  transition: all var(--transition-fast) !important;
  opacity: 1 !important;
}

.admin-link:hover {
  background: var(--color-text-secondary) !important;
}

/* 登录注册按钮样式 */
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

.page-content {
  padding-top: 76px;
}

/* 搜索区域 */
.search-section {
  background: var(--color-bg-primary);
  padding: 2rem 0;
  border-bottom: 1px solid var(--color-border);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.search-section h1 {
  color: var(--color-text-primary);
  margin-bottom: 2rem;
  text-align: center;
  font-size: 1.75rem;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.search-filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  align-items: end;
}

/* Element Plus 输入框样式覆盖 */
.search-filters :deep(.el-input__wrapper),
.search-filters :deep(.el-select .el-input__wrapper) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  box-shadow: none;
}

.search-filters :deep(.el-input__wrapper:hover),
.search-filters :deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--color-separator);
}

.search-filters :deep(.el-input.is-focus .el-input__wrapper),
.search-filters :deep(.el-select .el-input.is-focus .el-input__wrapper) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.filter-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* 房间列表区域 */
.rooms-section {
  padding: 3rem 0;
}

.rooms-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.results-info {
  font-size: 1rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* 排序控件样式 */
.sort-controls :deep(.el-select) {
  width: 160px;
}

.sort-controls :deep(.el-select .el-input__wrapper) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.room-item {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);
}

.room-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.room-gallery {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.room-item:hover .main-image {
  transform: scale(1.05);
}

.image-indicator {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: var(--color-text-primary);
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
  border: 1px solid var(--color-border);
}

.room-badges {
  position: absolute;
  top: 1rem;
  left: 1rem;
  display: flex;
  gap: 0.5rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.badge.hot {
  background: rgba(255, 59, 48, 0.9);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.badge.discount {
  background: rgba(255, 149, 0, 0.9);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.room-details {
  padding: 1.5rem;
}

/* 按钮样式 */
.action-buttons :deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
  padding: 0.625rem 1.25rem;
}

.action-buttons :deep(.el-button--default) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

.action-buttons :deep(.el-button--default:hover) {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

.action-buttons :deep(.el-button--primary) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

.action-buttons :deep(.el-button--primary:hover) {
  background: var(--color-primary-hover);
  border-color: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.action-buttons :deep(.el-button--primary:disabled) {
  background: var(--color-bg-tertiary);
  border-color: var(--color-bg-tertiary);
  color: var(--color-text-tertiary);
  opacity: 0.6;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.room-header h3 {
  color: var(--color-text-primary);
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
  letter-spacing: -0.01em;
}

.room-rating {
  text-align: right;
}

.stars {
  color: var(--color-warning);
  font-size: 0.875rem;
}

.rating-score {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  margin-left: 0.5rem;
  font-weight: 500;
}

.room-description {
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
  line-height: 1.6;
  font-size: 0.9375rem;
}

.room-specs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.spec-item {
  display: flex;
  justify-content: space-between;
}

.spec-label {
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
}

.spec-value {
  color: var(--color-text-primary);
  font-weight: 500;
  font-size: 0.875rem;
}

.room-amenities {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.amenity {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
}

.price-section {
  flex: 1;
}

.original-price {
  color: var(--color-text-tertiary);
  text-decoration: line-through;
  font-size: 0.875rem;
}

.current-price {
  margin: 0.2rem 0;
}

.price {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-text-primary);
  letter-spacing: -0.02em;
}

.unit {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 400;
}

.total-price {
  color: var(--color-text-secondary);
  font-size: 0.8125rem;
  margin-top: 0.25rem;
}

.action-buttons {
  display: flex;
  gap: 0.8rem;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
}

.pagination-wrapper :deep(.el-pagination) {
  font-weight: 500;
}

.pagination-wrapper :deep(.el-pager li) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  margin: 0 0.25rem;
  font-weight: 500;
  transition: all var(--transition-fast);
}

.pagination-wrapper :deep(.el-pager li:hover) {
  color: var(--color-primary);
  border-color: var(--color-primary);
}

.pagination-wrapper :deep(.el-pager li.is-active) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

/* 无结果提示 */
.no-results {
  text-align: center;
  padding: 4rem 2rem;
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  margin: 2rem auto;
  max-width: 500px;
}

.no-results-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.no-results h3 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-size: 1.25rem;
  font-weight: 600;
}

.no-results p {
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
  font-size: 0.9375rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .search-filters {
    grid-template-columns: 1fr;
  }

  .rooms-grid {
    grid-template-columns: 1fr;
  }

  .room-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .action-buttons {
    justify-content: stretch;
  }

  .action-buttons .el-button {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }

  .rooms-grid {
    gap: 1rem;
  }

  .room-item {
    margin: 0;
  }
}
</style>
