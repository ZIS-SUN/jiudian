<template>
  <div class="room-detail-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h2>🏨 星级酒店</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/my-bookings" class="nav-link">我的预订</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>
          <router-link to="/admin" class="nav-link admin-link">管理后台</router-link>
        </div>
      </div>
    </nav>

    <div class="page-content">
      <div class="container">
        <!-- 返回按钮 -->
        <div class="back-button">
          <el-button @click="goBack" icon="ArrowLeft">返回房间列表</el-button>
        </div>

        <!-- 房间详情 -->
        <div class="room-detail" v-if="room">
          <div class="room-gallery">
            <div class="main-image">
              <img :src="room.image || '/placeholder-room.jpg'" :alt="room.name" />
            </div>
          </div>

          <div class="room-info">
            <div class="room-header">
              <h1>{{ room.name }}</h1>
              <div class="room-price">
                <span class="price">￥{{ room.price }}</span>
                <span class="unit">/晚</span>
              </div>
            </div>

            <div class="room-features">
              <h3>房间特色</h3>
              <div class="feature-grid">
                <div class="feature-item">
                  <i class="el-icon-user"></i>
                  <span>最多入住 {{ room.capacity || 2 }} 人</span>
                </div>
                <div class="feature-item">
                  <i class="el-icon-house"></i>
                  <span>面积 {{ room.area || '35' }} 平方米</span>
                </div>
                <div class="feature-item">
                  <i class="el-icon-coffee"></i>
                  <span>免费WiFi</span>
                </div>
                <div class="feature-item">
                  <i class="el-icon-service"></i>
                  <span>24小时客房服务</span>
                </div>
              </div>
            </div>

            <div class="room-description">
              <h3>房间描述</h3>
              <p>{{ room.description || '舒适雅致的客房，配备现代化设施，为您提供完美的住宿体验。' }}</p>
            </div>

            <div class="room-amenities">
              <h3>房间设施</h3>
              <div class="amenity-list">
                <span class="amenity-tag">免费WiFi</span>
                <span class="amenity-tag">空调</span>
                <span class="amenity-tag">电视</span>
                <span class="amenity-tag">冰箱</span>
                <span class="amenity-tag">吹风机</span>
                <span class="amenity-tag">保险箱</span>
              </div>
            </div>

            <div class="booking-section">
              <el-button type="primary" size="large" @click="bookNow">
                立即预订
              </el-button>
            </div>
          </div>

          <!-- 客户评价 -->
          <RoomReviews :room-type-id="room.id" />
        </div>

        <!-- 加载状态 -->
        <div v-else class="loading">
          <el-skeleton :rows="8" animated />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RoomReviews from '@/components/RoomReviews.vue'

export default {
  name: 'RoomDetail',
  components: {
    RoomReviews
  },
  data() {
    return {
      room: null,
      loading: true
    }
  },
  mounted() {
    this.loadRoomDetail()
  },
  methods: {
    async loadRoomDetail() {
      try {
        // 从路由参数获取房间ID
        const roomId = this.$route.query.id
        if (!roomId) {
          this.$message.error('房间ID不存在')
          this.goBack()
          return
        }

        // 模拟API调用 - 你可以替换为真实的API
        setTimeout(() => {
          this.room = {
            id: roomId,
            name: '豪华标准间',
            price: 298,
            capacity: 2,
            area: 35,
            image: '/placeholder-room.jpg',
            description: '舒适雅致的豪华标准间，配备现代化设施，宽敞明亮，为您提供完美的住宿体验。房间内设有独立卫生间、空调、电视、WiFi等设施。'
          }
          this.loading = false
        }, 1000)
      } catch (error) {
        console.error('加载房间详情失败:', error)
        this.$message.error('加载房间详情失败')
        this.goBack()
      }
    },
    goBack() {
      this.$router.push('/rooms')
    },
    bookNow() {
      // 跳转到预订页面
      this.$router.push({
        path: '/booking',
        query: { roomId: this.room.id }
      })
    }
  }
}
</script>

<style scoped>
.room-detail-page {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

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
  margin: 0;
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
  padding-top: 100px;
  padding-bottom: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.back-button {
  margin-bottom: 2rem;
}

/* Element Plus 样式覆盖 */
:deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
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

:deep(.el-button--large) {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
}

:deep(.el-skeleton) {
  --el-skeleton-color: var(--color-bg-secondary);
  --el-skeleton-to-color: var(--color-bg-tertiary);
}

.room-detail {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.room-gallery {
  height: 400px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.room-detail:hover .main-image img {
  transform: scale(1.02);
}

.room-info {
  padding: 2rem;
}

.feature-item span {
  color: var(--color-text-primary);
  font-size: 0.9375rem;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.room-header h1 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.75rem;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.room-price {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.price {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: -0.02em;
}

.unit {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
}

.room-features,
.room-description,
.room-amenities {
  margin-bottom: 2rem;
}

.room-features h3,
.room-description h3,
.room-amenities h3 {
  margin-bottom: 1rem;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.feature-item i {
  color: var(--color-primary);
  font-size: 1.125rem;
}

.room-description p {
  line-height: 1.6;
  color: var(--color-text-secondary);
  margin: 0;
  font-size: 0.9375rem;
}

.amenity-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.amenity-tag {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-xl);
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid var(--color-border);
}

.booking-section {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid var(--color-border);
}

.loading {
  background: var(--color-bg-primary);
  padding: 2rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .room-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .room-gallery {
    height: 250px;
  }
  
  .room-info {
    padding: 1.5rem;
  }
}
</style>