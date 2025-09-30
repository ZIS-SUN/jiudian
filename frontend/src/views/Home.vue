<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand">
          <h1>酒店管理系统</h1>
        </div>
        <div class="nav-menu">
          <router-link to="/" class="nav-link active">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link nav-button">注册</router-link>
          </template>
          <template v-else>
            <router-link v-if="userStore.isLoggedIn" to="/my-bookings" class="nav-link">我的预订</router-link>
            <router-link v-if="userStore.isAdmin" to="/admin" class="nav-link">管理后台</router-link>
            <span class="nav-user">{{ userStore.userInfo?.username }}</span>
            <button @click="logout" class="nav-link btn-logout">退出</button>
          </template>
        </div>
      </div>
    </nav>

    <!-- 系统公告区域 -->
    <section v-if="announcements.length > 0" class="announcements-section">
      <div class="container">
        <h2 class="section-title">📢 系统公告</h2>
        <div class="announcements-list">
          <div
            v-for="announcement in announcements.slice(0, 3)"
            :key="announcement.id"
            class="announcement-card"
            :class="{'urgent': announcement.type === 'URGENT'}"
          >
            <div class="announcement-header">
              <span class="announcement-type" :class="getTypeClass(announcement.type)">
                {{ getTypeText(announcement.type) }}
              </span>
              <span v-if="announcement.priority === 1" class="announcement-priority">重要</span>
            </div>
            <h3 class="announcement-title">{{ announcement.title }}</h3>
            <p class="announcement-content">{{ announcement.content }}</p>
            <span class="announcement-time">{{ formatDate(announcement.createTime) }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Banner区域 -->
    <section class="banner-section">
      <div class="container">
        <div class="banner-content">
          <h1>欢迎使用酒店管理系统</h1>
          <p>提供优质的客房预订与管理服务</p>
          <div class="banner-buttons">
            <router-link to="/rooms" class="btn-primary">立即预订</router-link>
            <router-link to="/contact" class="btn-secondary">联系我们</router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能介绍 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">系统功能</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🏠</div>
            <h3>客房管理</h3>
            <p>多种房型选择，满足不同需求</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📅</div>
            <h3>在线预订</h3>
            <p>便捷的预订流程，快速确认</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">💳</div>
            <h3>支付管理</h3>
            <p>支持多种支付方式</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">👤</div>
            <h3>客户服务</h3>
            <p>专业的客户服务团队</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 酒店管理系统. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getActiveAnnouncements } from '@/api/announcements'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const announcements = ref([])

const loadAnnouncements = async () => {
  try {
    const res = await getActiveAnnouncements()
    announcements.value = res.data || []
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

const logout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/')
}

const getTypeClass = (type) => {
  const map = {
    NOTICE: 'type-notice',
    PROMOTION: 'type-promotion',
    MAINTENANCE: 'type-maintenance',
    URGENT: 'type-urgent'
  }
  return map[type] || 'type-notice'
}

const getTypeText = (type) => {
  const map = {
    NOTICE: '通知',
    PROMOTION: '优惠',
    MAINTENANCE: '维护',
    URGENT: '紧急'
  }
  return map[type] || type
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(() => {
  userStore.initUserInfo()
  loadAnnouncements()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 导航栏 */
.navbar {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.nav-brand h1 {
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.nav-link:hover, .nav-link.active {
  color: #409eff;
}

.nav-button {
  background: #409eff;
  color: #fff !important;
  padding: 6px 16px;
  border-radius: 3px;
}

.nav-button:hover {
  background: #66b1ff;
}

.nav-user {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.btn-logout {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

/* 公告区域 */
.announcements-section {
  padding: 30px 20px;
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
}

.section-title {
  text-align: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 40px;
  font-weight: 700;
  position: relative;
  padding-bottom: 16px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.announcements-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.announcement-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.announcement-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.announcement-card.urgent {
  border-left: 4px solid #f56c6c;
  background: linear-gradient(to right, #fef0f0 0%, #fff 10%);
}

.announcement-header {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.announcement-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 2px;
}

.type-notice {
  background: #ecf5ff;
  color: #409eff;
}

.type-promotion {
  background: #f0f9ff;
  color: #67c23a;
}

.type-maintenance {
  background: #fdf6ec;
  color: #e6a23c;
}

.type-urgent {
  background: #fef0f0;
  color: #f56c6c;
}

.announcement-priority {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 2px;
  background: #f56c6c;
  color: #fff;
}

.announcement-title {
  font-size: 15px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 600;
}

.announcement-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 10px;
}

.announcement-time {
  font-size: 12px;
  color: #909399;
}

/* Banner */
.banner-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 80px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.banner-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.05)" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,138.7C960,139,1056,117,1152,101.3C1248,85,1344,75,1392,69.3L1440,64L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
  background-size: cover;
  opacity: 0.3;
}

.banner-content {
  position: relative;
  z-index: 1;
}

.banner-content h1 {
  font-size: 36px;
  margin-bottom: 16px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 32px;
  opacity: 0.95;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.banner-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary {
  display: inline-block;
  background: #fff;
  color: #667eea;
  padding: 12px 32px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.btn-secondary {
  display: inline-block;
  background: transparent;
  color: #fff;
  padding: 12px 32px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  font-size: 15px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: #fff;
}

/* 功能 */
.features-section {
  padding: 50px 20px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.feature-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 32px 24px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: inline-block;
  transition: transform 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.feature-card h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.feature-card p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* Footer */
.footer {
  background: #303133;
  color: #fff;
  padding: 20px;
  text-align: center;
}

.footer p {
  font-size: 13px;
  opacity: 0.8;
}
</style>
