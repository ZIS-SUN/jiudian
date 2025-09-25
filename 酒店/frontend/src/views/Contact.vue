<template>
  <div class="contact-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h2>🏨 星级酒店</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/contact" class="nav-link active">联系我们</router-link>

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

    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-overlay"></div>
        <div class="hero-particles"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">联系我们</h1>
        <p class="hero-subtitle">
          有任何问题？我们在这里为您服务
        </p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">24小时</div>
            <div class="stat-label">前台服务</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">免费WiFi</div>
            <div class="stat-label">全区覆盖</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">免费停车</div>
            <div class="stat-label">充足车位</div>
          </div>
        </div>
      </div>
    </section>

    <div class="page-content">
      <div class="container">
        <!-- 周边景点推荐 -->
        <section class="attractions-section">
          <div class="section-header">
            <h2>周边景点推荐</h2>
            <p class="section-subtitle">酒店周边热门景点，步行或短途可达</p>
            
            <!-- 筛选标签 -->
            <div class="filter-tabs">
              <div 
                class="filter-tab" 
                :class="{ active: activeFilter === 'all' }"
                @click="setActiveFilter('all')"
              >
                <span class="tab-icon">🌟</span>
                全部景点
              </div>
              <div 
                class="filter-tab" 
                :class="{ active: activeFilter === 'culture' }"
                @click="setActiveFilter('culture')"
              >
                <span class="tab-icon">🏛️</span>
                文化古迹
              </div>
              <div 
                class="filter-tab" 
                :class="{ active: activeFilter === 'nature' }"
                @click="setActiveFilter('nature')"
              >
                <span class="tab-icon">🌿</span>
                自然风光
              </div>
              <div 
                class="filter-tab" 
                :class="{ active: activeFilter === 'entertainment' }"
                @click="setActiveFilter('entertainment')"
              >
                <span class="tab-icon">🎭</span>
                休闲娱乐
              </div>
            </div>
          </div>
          
          <div class="attractions-grid">
            <div 
              class="attraction-card" 
              v-for="attraction in filteredAttractions" 
              :key="attraction.id"
              @click="showAttractionDetail(attraction)"
            >
              <div class="attraction-image">
                <img :src="attraction.image" :alt="attraction.name" />
                <div class="attraction-overlay">
                  <div class="attraction-distance">
                    <i class="distance-icon">📍</i>
                    {{ attraction.distance }}
                  </div>
                  <div class="attraction-category">{{ attraction.category }}</div>
                </div>
                <div class="image-hover-overlay">
                  <div class="hover-content">
                    <div class="hover-icon">👁️</div>
                    <span>查看详情</span>
                  </div>
                </div>
              </div>
              <div class="attraction-info">
                <div class="info-header">
                  <h3>{{ attraction.name }}</h3>
                  <div class="popularity-indicator" v-if="attraction.rating >= 4.5">
                    <span class="popularity-text">热门</span>
                  </div>
                </div>
                <p class="attraction-desc">{{ attraction.description }}</p>
                <div class="attraction-features">
                  <span class="feature" v-for="feature in attraction.features.slice(0, 2)" :key="feature">
                    {{ feature }}
                  </span>
                </div>
                <div class="attraction-footer">
                  <div class="attraction-rating">
                    <div class="stars-container">
                      <span class="rating-stars">★★★★★</span>
                      <div class="rating-overlay" :style="{ width: (attraction.rating / 5 * 100) + '%' }">
                        ★★★★★
                      </div>
                    </div>
                    <span class="rating-text">{{ attraction.rating }}分</span>
                    <span class="rating-count">({{ attraction.reviewCount }}条评价)</span>
                  </div>
                  <div class="attraction-actions">
                    <div class="attraction-price">
                      <span class="price-label">门票</span>
                      <span class="price">{{ attraction.price }}</span>
                    </div>
                    <button class="book-btn" @click.stop="bookAttraction(attraction)">
                      <span class="btn-text">立即预订</span>
                      <i class="btn-arrow">→</i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 查看更多按钮 -->
          <div class="more-attractions">
            <button class="load-more-btn" @click="loadMoreAttractions">
              <span class="btn-content">
                <i class="load-icon">🔄</i>
                <span>加载更多景点</span>
              </span>
            </button>
          </div>
        </section>

        <div class="contact-layout">
          <!-- 联系信息 -->
          <div class="contact-info">
            <div class="info-card">
              <div class="info-icon">📍</div>
              <h3>酒店地址</h3>
              <p>北京市朝阳区建国路88号</p>
              <p>邮编：100024</p>
            </div>

            <div class="info-card">
              <div class="info-icon">📞</div>
              <h3>联系电话</h3>
              <p>前台：010-8888-8888</p>
              <p>预订：400-888-8888</p>
            </div>

            <div class="info-card">
              <div class="info-icon">📧</div>
              <h3>电子邮箱</h3>
              <p>service@starhotel.com</p>
            </div>

            <div class="info-card">
              <div class="info-icon">🕒</div>
              <h3>服务时间</h3>
              <p>前台：24小时服务</p>
              <p>客房：24小时服务</p>
            </div>
          </div>

          <!-- 留言表单 -->
          <div class="contact-form">
            <h2>在线留言</h2>
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="80px"
            >
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入您的姓名" />
              </el-form-item>

              <el-form-item label="电话" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入联系电话" />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱地址" />
              </el-form-item>

              <el-form-item label="主题" prop="subject">
                <el-select
                  v-model="form.subject"
                  placeholder="请选择咨询主题"
                  style="width: 100%"
                >
                  <el-option label="房间预订" value="booking" />
                  <el-option label="服务投诉" value="complaint" />
                  <el-option label="建议反馈" value="feedback" />
                  <el-option label="其他咨询" value="other" />
                </el-select>
              </el-form-item>

              <el-form-item label="留言" prop="message">
                <el-input
                  v-model="form.message"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述您的问题或建议..."
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  @click="submitForm"
                  :loading="submitting"
                  size="large"
                >
                  提交留言
                </el-button>
                <el-button @click="resetForm" size="large">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 景点预订弹窗 -->
    <AttractionBooking
      v-model="showBookingDialog"
      :attraction="selectedAttraction"
      @booking-success="handleBookingSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import AttractionBooking from "@/components/AttractionBooking.vue";

const formRef = ref();
const submitting = ref(false);
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

// 景点筛选状态
const activeFilter = ref('all')

// 周边景点数据
const attractions = ref([
  {
    id: 1,
    name: "故宫博物院",
    description: "明清两代的皇家宫殿",
    distance: "2.5km",
    rating: 4.8,
    reviewCount: 2850,
    price: "¥60起",
    category: "文化古迹",
    type: "culture",
    image: "https://images.unsplash.com/photo-1519677100203-a0e668c93bb6?w=600&h=400&fit=crop&crop=center",
    features: ["世界遗产", "历史文化"],
    openTime: "08:30-17:00"
  },
  {
    id: 2,
    name: "天坛公园",
    description: "明清皇帝祭天场所",
    distance: "3.8km",
    rating: 4.7,
    reviewCount: 1920,
    price: "¥35起",
    category: "文化古迹",
    type: "culture",
    image: "https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=600&h=400&fit=crop&crop=center",
    features: ["祭天建筑", "园林艺术"],
    openTime: "06:00-21:00"
  },
  {
    id: 3,
    name: "颐和园",
    description: "清代皇家园林",
    distance: "15km",
    rating: 4.9,
    reviewCount: 3420,
    price: "¥50起",
    category: "自然风光",
    type: "nature",
    image: "https://images.unsplash.com/photo-1523907438-beb0e5ecf2ed?w=600&h=400&fit=crop&crop=center",
    features: ["皇家园林", "湖光山色"],
    openTime: "06:30-18:00"
  },
  {
    id: 4,
    name: "慕田峪长城",
    description: "长城精华段",
    distance: "68km",
    rating: 4.8,
    reviewCount: 2680,
    price: "¥45起",
    category: "自然风光",
    type: "nature",
    image: "https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=600&h=400&fit=crop&crop=center",
    features: ["世界奇迹", "登高望远"],
    openTime: "07:30-17:30"
  },
  {
    id: 5,
    name: "什刹海",
    description: "传统胡同区",
    distance: "8km",
    rating: 4.6,
    reviewCount: 1560,
    price: "免费",
    category: "休闲娱乐",
    type: "entertainment",
    image: "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=600&h=400&fit=crop&crop=center",
    features: ["胡同文化", "酒吧街"],
    openTime: "全天开放"
  },
  {
    id: 6,
    name: "雍和宫",
    description: "藏传佛教寺院",
    distance: "12km",
    rating: 4.5,
    reviewCount: 1340,
    price: "¥25起",
    category: "文化古迹",
    type: "culture",
    image: "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600&h=400&fit=crop&crop=center",
    features: ["藏传佛教", "古建筑"],
    openTime: "09:00-16:30"
  },
  {
    id: 7,
    name: "北海公园",
    description: "皇家园林",
    distance: "6km",
    rating: 4.4,
    reviewCount: 980,
    price: "¥10起",
    category: "自然风光",
    type: "nature",
    image: "https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=600&h=400&fit=crop&crop=center",
    features: ["古典园林", "白塔"],
    openTime: "06:30-20:00"
  },
  {
    id: 8,
    name: "798艺术区",
    description: "当代艺术园区",
    distance: "18km",
    rating: 4.3,
    reviewCount: 1680,
    price: "免费",
    category: "休闲娱乐",
    type: "entertainment",
    image: "https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=600&h=400&fit=crop&crop=center",
    features: ["当代艺术", "画廊"],
    openTime: "10:00-18:00"
  }
])

// 筛选后的景点列表
const filteredAttractions = computed(() => {
  if (activeFilter.value === 'all') {
    return attractions.value
  }
  return attractions.value.filter(attraction => attraction.type === activeFilter.value)
})

// 设置筛选条件
const setActiveFilter = (filter) => {
  activeFilter.value = filter
}

// 查看景点详情
const showAttractionDetail = (attraction) => {
  console.log('查看景点详情:', attraction)
  ElMessage.success(`正在跳转到${attraction.name}详情页面`)
}

// 预订景点门票
const showBookingDialog = ref(false)
const selectedAttraction = ref(null)

const bookAttraction = (attraction) => {
  selectedAttraction.value = attraction
  showBookingDialog.value = true
}

const handleBookingSuccess = (bookingInfo) => {
  ElMessage.success(`${bookingInfo.attraction.name}门票预订成功！`)
  // 可以在这里添加更多成功后的处理逻辑
}

// 加载更多景点
const loadMoreAttractions = () => {
  ElMessage.info('加载更多景点功能开发中')
}

const form = reactive({
  name: "",
  phone: "",
  email: "",
  subject: "",
  message: "",
});

const rules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  phone: [
    { required: true, message: "请输入电话", trigger: "blur" },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
  ],
  subject: [{ required: true, message: "请选择咨询主题", trigger: "change" }],
  message: [
    { required: true, message: "请输入留言内容", trigger: "blur" },
    { min: 10, message: "留言内容至少10个字符", trigger: "blur" },
  ],
};

const submitForm = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitting.value = true;

    // 模拟提交
    await new Promise((resolve) => setTimeout(resolve, 2000));

    ElMessage.success("留言提交成功！我们会尽快回复您。");
    resetForm();
  } catch (error) {
    console.error("表单验证失败:", error);
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 页面加载时初始化用户信息
onMounted(() => {
  userStore.initUserInfo();
});
</script>

<style scoped>
.contact-page {
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

/* 英雄区域 - Apple Design */
.hero-section {
  position: relative;
  height: 50vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
  margin-top: 76px;
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.05), rgba(52, 199, 89, 0.05)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1920&h=1080&fit=crop&crop=center")
      center/cover;
  opacity: 0.3;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--color-bg-primary);
  opacity: 0.8;
}

.hero-content {
  max-width: 1000px;
  padding: 2rem;
  z-index: 1;
}

/* 删除不需要的hero-badge样式 */

.hero-title {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
  letter-spacing: -0.02em;
}

.hero-subtitle {
  font-size: 1.125rem;
  line-height: 1.6;
  margin-bottom: 2rem;
  color: var(--color-text-secondary);
  font-weight: 400;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 600;
  display: block;
  margin-bottom: 0.5rem;
  color: var(--color-text-primary);
}

.stat-label {
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-text-secondary);
  font-weight: 500;
}


.page-content {
  padding: 2rem 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 景点区域 */
.attractions-section {
  margin-bottom: 6rem;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

/* 简化section-badge样式 */

/* 筛选标签样式 - Apple Design */
.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 0.75rem;
  margin-top: 2rem;
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
  color: var(--color-text-secondary);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.filter-tab:hover {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

.filter-tab.active {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}

.tab-icon {
  font-size: 1rem;
}

.section-header h2 {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin-bottom: 1rem;
  font-weight: 600;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.section-subtitle {
  font-size: 1rem;
  color: var(--color-text-secondary);
  line-height: 1.6;
  font-weight: 400;
}

.attractions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 2rem;
}

.attraction-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);
  position: relative;
  cursor: pointer;
}

.attraction-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.attraction-image {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.attraction-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.attraction-card:hover .attraction-image img {
  transform: scale(1.05);
}

.attraction-overlay {
  position: absolute;
  top: 1rem;
  left: 1rem;
  right: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  z-index: 2;
}

.attraction-distance {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: var(--color-text-primary);
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
  border: 1px solid var(--color-border);
}

.distance-icon {
  font-size: 0.875rem;
}

.attraction-category {
  background: var(--color-primary);
  color: white;
  padding: 0.25rem 0.625rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* 简化悬浮效果 */
.image-hover-overlay {
  display: none;
}

.attraction-info {
  padding: 1.5rem;
  position: relative;
  z-index: 2;
}

/* 信息头部 */
.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.attraction-info h3 {
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  line-height: 1.3;
  margin: 0;
  flex: 1;
  letter-spacing: -0.01em;
}

.popularity-indicator {
  display: inline-block;
  background: #ff6b35;
  color: white;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.popularity-text {
  color: white;
}

.attraction-desc {
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
  line-height: 1.6;
  font-size: 0.875rem;
}

.attraction-features {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.feature {
  display: inline-block;
  background: #f0f0f0;
  color: #666;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}

.attraction-footer {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.attraction-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* 星级评分显示 - Apple Design */
.stars-container {
  position: relative;
  font-size: 0.875rem;
  line-height: 1;
}

.rating-stars {
  color: var(--color-border);
}

.rating-overlay {
  position: absolute;
  top: 0;
  left: 0;
  color: var(--color-warning);
  overflow: hidden;
  white-space: nowrap;
}

.rating-text {
  color: var(--color-text-primary);
  font-size: 0.875rem;
  font-weight: 500;
}

.rating-count {
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
  font-weight: 400;
}

.attraction-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.attraction-price {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.price-label {
  font-size: 0.75rem;
  color: var(--color-text-tertiary);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.attraction-price .price {
  color: var(--color-primary);
  font-size: 1.125rem;
  font-weight: 600;
  line-height: 1;
  letter-spacing: -0.01em;
}

/* 预订按钮 - Apple Design */
.book-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: var(--color-primary);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-md);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

.book-btn:hover {
  background: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-text {
  position: relative;
}

.btn-arrow {
  font-size: 0.875rem;
  transition: transform var(--transition-fast);
}

.book-btn:hover .btn-arrow {
  transform: translateX(2px);
}

.contact-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  align-items: start;
  margin-top: 3rem;
}

.contact-info {
  display: grid;
  gap: 1.5rem;
}

.info-card {
  background: var(--color-bg-primary);
  padding: 2rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  text-align: center;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.info-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.info-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
  display: block;
}

.info-card h3 {
  color: var(--color-text-primary);
  margin-bottom: 0.75rem;
  font-size: 1.125rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.info-card p {
  color: var(--color-text-secondary);
  margin: 0.5rem 0;
  font-size: 0.875rem;
  line-height: 1.6;
}

.contact-form {
  background: var(--color-bg-primary);
  padding: 2rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.contact-form h2 {
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

/* Element Plus 组件 - Apple Design */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  box-shadow: none;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--color-separator);
}

:deep(.el-input.is-focus .el-input__wrapper),
:deep(.el-select .el-input.is-focus .el-input__wrapper) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

:deep(.el-textarea__inner) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-textarea__inner:hover) {
  border-color: var(--color-separator);
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

:deep(.el-button--primary) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  border-radius: var(--radius-md);
  font-weight: 500;
  padding: 0.625rem 1.25rem;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

:deep(.el-button--primary:hover) {
  background: var(--color-primary-hover);
  border-color: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

:deep(.el-button--default) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-weight: 500;
  padding: 0.625rem 1.25rem;
  transition: all var(--transition-fast);
}

:deep(.el-button--default:hover) {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    max-width: 1200px;
    padding: 0 1.5rem;
  }
  
  .hero-title {
    font-size: 3rem;
  }
  
  .hero-stats {
    gap: 2rem;
  }
}

@media (max-width: 992px) {
  .nav-container {
    padding: 0 1.5rem;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    gap: 1.5rem;
  }
  
  .attractions-grid {
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 2rem;
  }
  
  .contact-layout {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  
  .hero-section {
    height: 60vh;
    margin-top: 70px;
  }
  
  .hero-title {
    font-size: 2.2rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 1rem;
  }
  
  .section-header h2 {
    font-size: 2.2rem;
  }
  
  .attractions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }
  
  .hero-title {
    font-size: 1.8rem;
  }
  
  .section-header h2 {
    font-size: 1.8rem;
  }
  
  .info-card,
  .contact-form {
    padding: 1.5rem;
  }
  
  .attraction-info {
    padding: 1.5rem;
  }
}

/* 加载更多按钮 */
.more-attractions {
  text-align: center;
  margin-top: 3rem;
}

.load-more-btn {
  display: inline-block;
  background: var(--color-primary);
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 8px;
  font-weight: 500;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.load-icon {
  font-size: 1rem;
}


.load-more-btn:hover {
  background: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 简化动画效果 */
.attraction-card,
.info-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

/* 性能优化 */
* {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.hero-background,
.hero-particles {
  will-change: transform;
}

.attraction-card,
.info-card {
  will-change: transform;
}
</style>
