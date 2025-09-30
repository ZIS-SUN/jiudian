<template>
  <div class="my-bookings-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h2>🏨 星级酒店</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>
          <router-link to="/my-bookings" class="nav-link active">我的预订</router-link>

          <!-- 登录后显示用户信息和登出 -->
          <span class="user-info">{{ userInfo?.name || userInfo?.username }}</span>
          <button class="logout-btn" @click="handleLogout">退出登录</button>

          <!-- 管理员专用链接 -->
          <router-link v-if="isAdmin" to="/admin" class="nav-link admin-link">管理后台</router-link>
        </div>
      </div>
    </nav>

    <div class="page-content">
      <div class="container">
        <div class="page-header">
          <h1>我的预订</h1>
          <p>查看您的预订历史和当前预订状态</p>
        </div>

        <!-- 查询订单 -->
        <div class="search-section">
          <div class="search-form">
            <el-input
              v-model="searchForm.bookingNumber"
              placeholder="请输入预订号查询"
              class="search-input"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-input
              v-model="searchForm.phone"
              placeholder="请输入手机号查询"
              class="search-input"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
            <el-button
              type="primary"
              @click="searchBookings"
              :loading="searching"
            >
              查询预订
            </el-button>
          </div>
        </div>

        <!-- 预订状态过滤 -->
        <div class="filter-section">
          <div class="filter-tabs">
            <button
              v-for="status in statusFilters"
              :key="status.value"
              :class="['filter-tab', { active: activeFilter === status.value }]"
              @click="setActiveFilter(status.value)"
            >
              <span class="tab-icon">{{ status.icon }}</span>
              <span>{{ status.label }}</span>
              <span class="tab-count" v-if="getFilterCount(status.value) > 0">
                {{ getFilterCount(status.value) }}
              </span>
            </button>
          </div>
        </div>

        <!-- 预订列表 -->
        <div class="bookings-section" v-loading="loading">
          <div
            v-if="filteredBookings.length === 0 && !loading"
            class="empty-state"
          >
            <div class="empty-icon">📝</div>
            <h3>暂无预订记录</h3>
            <p>
              您还没有任何预订记录，<router-link to="/rooms"
                >立即预订</router-link
              >您的理想客房！
            </p>
          </div>

          <div v-else class="booking-list">
            <div
              v-for="booking in filteredBookings"
              :key="booking.id"
              class="booking-card"
            >
              <div class="booking-header">
                <div class="booking-number">
                  <span class="label">预订号：</span>
                  <span class="number">{{ booking.bookingNumber }}</span>
                </div>
                <div class="booking-status">
                  <span :class="['status-badge', booking.status.toLowerCase()]">
                    {{ booking.statusText }}
                  </span>
                </div>
              </div>

              <div class="booking-content">
                <div class="room-info">
                  <img :src="booking.roomImage" :alt="booking.roomName" />
                  <div class="room-details">
                    <h3>{{ booking.roomName }}</h3>
                    <div class="booking-dates">
                      <span class="date">{{
                        formatDate(booking.checkInDate)
                      }}</span>
                      <span class="separator">-</span>
                      <span class="date">{{
                        formatDate(booking.checkOutDate)
                      }}</span>
                    </div>
                    <div class="booking-info">
                      <span>{{ booking.nights }}晚</span>
                      <span class="separator">·</span>
                      <span>{{ booking.roomCount }}间房</span>
                      <span class="separator">·</span>
                      <span>{{ booking.guestCount }}位客人</span>
                    </div>
                  </div>
                </div>

                <div class="booking-price">
                  <div class="total-amount">
                    <span class="currency">¥</span>
                    <span class="amount">{{
                      booking.totalAmount.toLocaleString()
                    }}</span>
                  </div>
                  <div class="price-note">含税费</div>
                </div>
              </div>

              <div class="booking-footer">
                <div class="booking-actions">
                  <el-button size="default" @click="viewDetails(booking)">
                    查看详情
                  </el-button>

                  <el-button
                    v-if="canCancel(booking)"
                    type="danger"
                    size="default"
                    @click="cancelBooking(booking)"
                  >
                    取消预订
                  </el-button>

                  <el-button
                    v-if="canModify(booking)"
                    type="warning"
                    size="default"
                    @click="modifyBooking(booking)"
                  >
                    修改预订
                  </el-button>

                  <el-button
                    v-if="canCheckIn(booking)"
                    type="success"
                    size="default"
                    @click="checkIn(booking)"
                  >
                    办理入住
                  </el-button>

                  <el-button
                    v-if="canReview(booking)"
                    type="primary"
                    size="default"
                    @click="addReview(booking)"
                  >
                    写评价
                  </el-button>
                </div>

                <div class="booking-meta">
                  <span
                    >预订时间：{{ formatDateTime(booking.createTime) }}</span
                  >
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="totalBookings > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="totalBookings"
            layout="prev, pager, next, jumper"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>

    <!-- 预订详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预订详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedBooking" class="booking-detail">
        <div class="detail-section">
          <h4>预订信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">预订号</span>
              <span class="value">{{ selectedBooking.bookingNumber }}</span>
            </div>
            <div class="detail-item">
              <span class="label">预订状态</span>
              <span
                :class="[
                  'value',
                  'status',
                  selectedBooking.status.toLowerCase(),
                ]"
              >
                {{ selectedBooking.statusText }}
              </span>
            </div>
            <div class="detail-item">
              <span class="label">房型</span>
              <span class="value">{{ selectedBooking.roomName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">入住日期</span>
              <span class="value">{{
                formatDate(selectedBooking.checkInDate)
              }}</span>
            </div>
            <div class="detail-item">
              <span class="label">退房日期</span>
              <span class="value">{{
                formatDate(selectedBooking.checkOutDate)
              }}</span>
            </div>
            <div class="detail-item">
              <span class="label">住宿天数</span>
              <span class="value">{{ selectedBooking.nights }}晚</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>客户信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">姓名</span>
              <span class="value">{{ selectedBooking.customerName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">手机号</span>
              <span class="value">{{ selectedBooking.customerPhone }}</span>
            </div>
            <div class="detail-item">
              <span class="label">邮箱</span>
              <span class="value">{{ selectedBooking.customerEmail }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>费用明细</h4>
          <div class="price-detail">
            <div class="price-item">
              <span>房费 ({{ selectedBooking.nights }}晚)</span>
              <span>¥{{ selectedBooking.roomFee.toLocaleString() }}</span>
            </div>
            <div class="price-item">
              <span>服务费</span>
              <span>¥{{ selectedBooking.serviceFee.toLocaleString() }}</span>
            </div>
            <div class="price-item">
              <span>税费</span>
              <span>¥{{ selectedBooking.taxFee.toLocaleString() }}</span>
            </div>
            <div class="price-item total">
              <span>总计</span>
              <span>¥{{ selectedBooking.totalAmount.toLocaleString() }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Phone } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { mockBookings, getUserBookings } from "@/data/mockData";

const router = useRouter();
const userStore = useUserStore();

// 用户状态 - 使用计算属性保持响应性
const userInfo = computed(() => userStore.userInfo);
const isAdmin = computed(() => userStore.isAdmin);

// 登出处理
const handleLogout = () => {
  userStore.logout();
  ElMessage.success('已退出登录');
  router.push('/');
};

// 状态管理
const loading = ref(false);
const searching = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const activeFilter = ref("all");
const detailDialogVisible = ref(false);
const selectedBooking = ref(null);

// 搜索表单
const searchForm = ref({
  bookingNumber: "",
  phone: "",
});

// 状态过滤器
const statusFilters = [
  { value: "all", label: "全部", icon: "📋" },
  { value: "PENDING", label: "待确认", icon: "⏳" },
  { value: "CONFIRMED", label: "已确认", icon: "✅" },
  { value: "CHECKED_IN", label: "已入住", icon: "🏨" },
  { value: "CHECKED_OUT", label: "已退房", icon: "✔️" },
  { value: "CANCELLED", label: "已取消", icon: "❌" },
];

// 根据当前用户获取预订记录
const loadUserBookings = () => {
  const currentUserId = userInfo.value?.id;
  if (currentUserId) {
    // 获取当前用户的预订记录 - 从localStorage和mockBookings合并
    const localBookings = JSON.parse(localStorage.getItem('userBookings') || '[]');
    const mockUserBookings = getUserBookings(currentUserId);

    // 合并两个数据源，并去重
    const allBookings = [...localBookings, ...mockUserBookings];
    const uniqueBookings = allBookings.filter((booking, index, self) =>
      index === self.findIndex(b => b.bookingNumber === booking.bookingNumber)
    );

    return uniqueBookings.map(booking => ({
      id: booking.id,
      bookingNumber: booking.bookingNumber,
      status: (booking.status || 'pending').toUpperCase(),
      statusText: getStatusText(booking.status || 'pending'),
      roomName: booking.roomTypeName,
      roomImage: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=300&h=200&fit=crop',
      checkInDate: booking.checkInDate,
      checkOutDate: booking.checkOutDate,
      nights: booking.nights,
      roomCount: booking.roomCount,
      guestCount: booking.guestCount,
      totalAmount: booking.totalAmount,
      roomFee: Math.round(booking.totalAmount * 0.87),
      serviceFee: Math.round(booking.totalAmount * 0.08),
      taxFee: Math.round(booking.totalAmount * 0.05),
      customerName: booking.userName,
      customerPhone: booking.userPhone,
      customerEmail: booking.userEmail,
      createTime: booking.bookingTime,
      specialRequests: booking.specialRequests || '',
      paymentMethod: booking.paymentMethod,
      paymentStatus: booking.paymentStatus,
      transactionId: booking.transactionId || ''
    }));
  } else {
    // 如果没有用户信息，返回空数组
    return [];
  }
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'checkedin': '已入住',
    'completed': '已完成',
    'cancelled': '已取消'
  };
  return statusMap[status] || '未知状态';
};

// 预订数据
const allBookings = ref([
  {
    id: 1,
    bookingNumber: "HT20241201001",
    status: "CONFIRMED",
    statusText: "已确认",
    roomName: "豪华海景大床房",
    roomImage:
      "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=300&h=200&fit=crop",
    checkInDate: "2024-12-15",
    checkOutDate: "2024-12-18",
    nights: 3,
    roomCount: 1,
    guestCount: 2,
    totalAmount: 2380,
    roomFee: 2064,
    serviceFee: 103,
    taxFee: 62,
    customerName: "张三",
    customerPhone: "13800138000",
    customerEmail: "zhangsan@example.com",
    createTime: "2024-12-01 14:30:00",
  },
  {
    id: 2,
    bookingNumber: "HT20241202001",
    status: "PENDING",
    statusText: "待确认",
    roomName: "商务套房",
    roomImage:
      "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=300&h=200&fit=crop",
    checkInDate: "2024-12-20",
    checkOutDate: "2024-12-22",
    nights: 2,
    roomCount: 1,
    guestCount: 1,
    totalAmount: 2138,
    roomFee: 1976,
    serviceFee: 99,
    taxFee: 59,
    customerName: "李四",
    customerPhone: "13900139000",
    customerEmail: "lisi@example.com",
    createTime: "2024-12-02 09:15:00",
  },
  {
    id: 3,
    bookingNumber: "HT20241125001",
    status: "CHECKED_OUT",
    statusText: "已退房",
    roomName: "家庭亲子房",
    roomImage:
      "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=300&h=200&fit=crop",
    checkInDate: "2024-11-25",
    checkOutDate: "2024-11-28",
    nights: 3,
    roomCount: 1,
    guestCount: 4,
    totalAmount: 2894,
    roomFee: 2664,
    serviceFee: 133,
    taxFee: 80,
    customerName: "王五",
    customerPhone: "13700137000",
    customerEmail: "wangwu@example.com",
    createTime: "2024-11-20 16:45:00",
  },
]);

// 计算属性
const filteredBookings = computed(() => {
  let bookings = allBookings.value;

  // 状态过滤
  if (activeFilter.value !== "all") {
    bookings = bookings.filter(
      (booking) => booking.status === activeFilter.value
    );
  }

  // 搜索过滤
  if (searchForm.value.bookingNumber) {
    bookings = bookings.filter((booking) =>
      booking.bookingNumber
        .toLowerCase()
        .includes(searchForm.value.bookingNumber.toLowerCase())
    );
  }

  if (searchForm.value.phone) {
    bookings = bookings.filter((booking) =>
      booking.customerPhone.includes(searchForm.value.phone)
    );
  }

  return bookings;
});

const totalBookings = computed(() => filteredBookings.value.length);

// 方法
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN", {
    month: "long",
    day: "numeric",
  });
};

const formatDateTime = (dateTimeString) => {
  const date = new Date(dateTimeString);
  return date.toLocaleString("zh-CN");
};

const getFilterCount = (status) => {
  if (status === "all") return allBookings.value.length;
  return allBookings.value.filter((booking) => booking.status === status)
    .length;
};

const setActiveFilter = (status) => {
  activeFilter.value = status;
  currentPage.value = 1;
};

const searchBookings = () => {
  searching.value = true;
  // 模拟搜索延迟
  setTimeout(() => {
    searching.value = false;
    currentPage.value = 1;
  }, 500);
};

const handlePageChange = (page) => {
  currentPage.value = page;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const viewDetails = (booking) => {
  selectedBooking.value = booking;
  detailDialogVisible.value = true;
};

const canCancel = (booking) => {
  return ["PENDING", "CONFIRMED"].includes(booking.status);
};

const canModify = (booking) => {
  return ["PENDING", "CONFIRMED"].includes(booking.status);
};

const canCheckIn = (booking) => {
  return (
    booking.status === "CONFIRMED" &&
    new Date(booking.checkInDate) <= new Date()
  );
};

const canReview = (booking) => {
  return booking.status === "CHECKED_OUT";
};

const cancelBooking = async (booking) => {
  try {
    await ElMessageBox.confirm(
      "确定要取消这个预订吗？取消后可能产生费用。",
      "取消预订",
      {
        confirmButtonText: "确定取消",
        cancelButtonText: "暂不取消",
        type: "warning",
      }
    );

    // 模拟取消预订
    booking.status = "CANCELLED";
    booking.statusText = "已取消";

    ElMessage.success("预订已取消");
  } catch {
    // 用户取消操作
  }
};

const modifyBooking = (booking) => {
  ElMessage.info("修改预订功能开发中...");
};

const checkIn = (booking) => {
  ElMessage.success("入住办理成功！");
  booking.status = "CHECKED_IN";
  booking.statusText = "已入住";
};

const addReview = (booking) => {
  ElMessage.info("评价功能开发中...");
};

// 处理localStorage变化
const handleStorageChange = (event) => {
  if (event.key === 'userBookings' && userInfo.value) {
    const userBookings = loadUserBookings();
    allBookings.value = userBookings;
  }
}

// 页面获得焦点时刷新数据
const handleVisibilityChange = () => {
  if (!document.hidden && userInfo.value) {
    const userBookings = loadUserBookings();
    allBookings.value = userBookings;
  }
}

// 定时刷新数据
const refreshData = () => {
  if (userInfo.value) {
    const userBookings = loadUserBookings();
    allBookings.value = userBookings;
  }
}

// 初始化
onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo();

  loading.value = true;
  // 立即加载数据而不是延迟
  const userBookings = loadUserBookings();
  allBookings.value = userBookings;
  loading.value = false;

  // 监听localStorage变化
  window.addEventListener('storage', handleStorageChange);
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange);

  // 每30秒刷新一次数据
  const refreshInterval = setInterval(refreshData, 30000);

  // 保存interval引用以便清理
  window.myBookingsRefreshInterval = refreshInterval;
});

// 页面卸载时清理事件监听
onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange);
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  if (window.myBookingsRefreshInterval) {
    clearInterval(window.myBookingsRefreshInterval);
  }
});
</script>

<style scoped>
.my-bookings-page {
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

.page-content {
  padding-top: 76px;
  padding-bottom: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 2rem;
  padding: 2rem 0;
}

.page-header h1 {
  color: var(--color-text-primary);
  font-size: 2rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.page-header p {
  color: var(--color-text-secondary);
  font-size: 1rem;
}

/* 搜索区域 */
.search-section {
  background: var(--color-bg-primary);
  padding: 1.5rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  margin-bottom: 2rem;
}

.search-form {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}

.search-input {
  width: 250px;
}

/* Element Plus 样式覆盖 */
:deep(.el-input__wrapper) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  box-shadow: none;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--color-separator);
}

:deep(.el-input.is-focus .el-input__wrapper) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

:deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
  padding: 0.5rem 1rem;
}

:deep(.el-button--default) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

:deep(.el-button--default:hover) {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

:deep(.el-button--primary) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

:deep(.el-button--primary:hover) {
  background: var(--color-primary-hover);
  border-color: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

:deep(.el-button--danger) {
  background: var(--color-danger);
  border-color: var(--color-danger);
}

:deep(.el-button--warning) {
  background: var(--color-warning);
  border-color: var(--color-warning);
}

:deep(.el-button--success) {
  background: var(--color-success);
  border-color: var(--color-success);
}

:deep(.el-dialog) {
  border-radius: var(--radius-lg);
}

:deep(.el-dialog__header) {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-dialog__title) {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
  letter-spacing: -0.01em;
}

:deep(.el-dialog__body) {
  padding: 1.5rem;
}

/* 过滤标签 - Apple Design */
.filter-section {
  margin-bottom: 2rem;
}

.filter-tabs {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.filter-tab:hover {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

.filter-tab.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.tab-icon {
  font-size: 1rem;
}

.tab-count {
  background: var(--color-bg-tertiary);
  padding: 0.125rem 0.375rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
}

.filter-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.2);
}

/* 预订列表 */
.bookings-section {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-state h3 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-weight: 600;
  font-size: 1.25rem;
}

.empty-state p {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
}

.empty-state a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.booking-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  transition: all var(--transition-base);
  border: 1px solid var(--color-border);
}

.booking-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border);
}

.booking-number .label {
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
}

.booking-number .number {
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 1rem;
  letter-spacing: -0.01em;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.pending {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

.status-badge.confirmed {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

.status-badge.checked_in {
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-primary);
}

.status-badge.checked_out {
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
}

.status-badge.cancelled {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

.booking-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
}

.room-info {
  display: flex;
  gap: 1.5rem;
  flex: 1;
}

.room-info img {
  width: 120px;
  height: 90px;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.room-details h3 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-size: 1.125rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.booking-dates {
  margin-bottom: 0.5rem;
  font-size: 0.9375rem;
  color: var(--color-text-primary);
}

.date {
  font-weight: 500;
}

.separator {
  color: var(--color-text-tertiary);
  margin: 0 0.5rem;
}

.booking-info {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.booking-price {
  text-align: right;
}

.total-amount {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  margin-bottom: 0.2rem;
}

.currency {
  font-size: 0.875rem;
  color: var(--color-primary);
}

.amount {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: -0.02em;
}

.price-note {
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
}

.booking-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background: var(--color-bg-secondary);
  border-top: 1px solid var(--color-border);
}

.booking-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.booking-meta {
  color: var(--color-text-tertiary);
  font-size: 0.8125rem;
}

/* 预订详情弹窗 */
.booking-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 1.5rem;
}

.detail-section h4 {
  color: var(--color-text-primary);
  margin-bottom: 1rem;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
}

.detail-item .label {
  color: var(--color-text-secondary);
  font-weight: 500;
  font-size: 0.875rem;
}

.detail-item .value {
  color: var(--color-text-primary);
  font-weight: 500;
  font-size: 0.875rem;
}

.detail-item .value.status {
  padding: 0.25rem 0.625rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
}

.price-detail {
  background: var(--color-bg-secondary);
  padding: 1.25rem;
  border-radius: var(--radius-md);
}

.price-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
}

.price-item:last-child {
  border-bottom: none;
}

.price-item.total {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 1rem;
  border-top: 2px solid var(--color-primary);
  margin-top: 0.5rem;
  padding-top: 0.75rem;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .filter-tabs {
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 0.5rem;
  }

  .filter-tab {
    white-space: nowrap;
  }

  .booking-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .room-info {
    flex-direction: column;
    text-align: center;
  }

  .room-info img {
    align-self: center;
  }

  .booking-price {
    text-align: left;
    width: 100%;
  }

  .booking-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .booking-actions {
    justify-content: stretch;
  }

  .booking-actions .el-button {
    flex: 1;
  }

  .booking-meta {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }

  .page-header h1 {
    font-size: 2rem;
  }

  .search-section,
  .booking-card {
    margin-left: -0.5rem;
    margin-right: -0.5rem;
  }

  .booking-header,
  .booking-content,
  .booking-footer {
    padding: 1rem;
  }

  .room-info {
    gap: 1rem;
  }

  .total-amount .amount {
    font-size: 1.5rem;
  }
}
</style>
