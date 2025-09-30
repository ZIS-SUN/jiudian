<template>
  <div class="modern-home">
    <!-- 使用现代导航组件 -->
    <ModernNav />

    <!-- AI聊天机器人 -->
    <ChatBot />

    <!-- 现代英雄区域 -->
    <section class="hero-modern">
      <div class="hero-bg">
        <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1920&h=1080&fit=crop" alt="Hotel" />
        <div class="hero-gradient"></div>
      </div>

      <div class="hero-content">
        <div class="hero-text fade-in">
          <h1 class="hero-title">
            <span class="title-main">欢迎来到星悦酒店</span>
            <span class="title-sub">体验奢华，感受温馨</span>
          </h1>
          <p class="hero-desc">
            在城市中心，为您打造舒适的居住空间
          </p>

          <div class="hero-cta">
            <router-link to="/rooms" class="btn-hero btn-primary-hero">
              立即预订
              <svg class="btn-arrow" width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M5 10H15M15 10L10 5M15 10L10 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </router-link>
            <router-link to="/promotions" class="btn-hero btn-ghost-hero">
              查看优惠
            </router-link>
          </div>
        </div>

        <!-- 快速预订表单 -->
        <div class="booking-card fade-in-delay">
          <h3>快速预订</h3>
          <form class="booking-form">
            <div class="form-row">
              <div class="form-field">
                <label>入住日期</label>
                <input type="date" v-model="checkIn" class="form-input" />
              </div>
              <div class="form-field">
                <label>退房日期</label>
                <input type="date" v-model="checkOut" class="form-input" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-field">
                <label>客人数量</label>
                <select v-model="guests" class="form-input">
                  <option value="1">1位客人</option>
                  <option value="2">2位客人</option>
                  <option value="3">3位客人</option>
                  <option value="4">4位客人</option>
                </select>
              </div>
            </div>
            <button type="submit" class="btn-submit">
              查找房间
            </button>
          </form>
        </div>
      </div>
    </section>

    <!-- 特色服务 -->
    <section class="features-section">
      <div class="container">
        <div class="section-header">
          <h2>酒店特色</h2>
          <p>为您提供全方位的优质服务</p>
        </div>

        <div class="features-grid">
          <div class="feature-card slide-in-left">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="20" fill="url(#grad1)" opacity="0.1"/>
                <path d="M24 12L26.5 19H34L28 23L30.5 30L24 26L17.5 30L20 23L14 19H21.5L24 12Z" stroke="url(#grad1)" stroke-width="2" fill="none"/>
                <defs>
                  <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#4A90E2"/>
                    <stop offset="100%" style="stop-color:#357ABD"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h3>五星级服务</h3>
            <p>国际标准的奢华体验</p>
          </div>

          <div class="feature-card slide-in-left" style="animation-delay: 0.1s">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="20" fill="url(#grad2)" opacity="0.1"/>
                <path d="M16 24C16 19.58 19.58 16 24 16C28.42 16 32 19.58 32 24C32 28.42 28.42 32 24 32C19.58 32 16 28.42 16 24Z" stroke="url(#grad2)" stroke-width="2" fill="none"/>
                <path d="M24 20V24L27 27" stroke="url(#grad2)" stroke-width="2" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="grad2" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#28A745"/>
                    <stop offset="100%" style="stop-color:#20863A"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h3>24小时服务</h3>
            <p>随时为您提供帮助</p>
          </div>

          <div class="feature-card slide-in-left" style="animation-delay: 0.2s">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="20" fill="url(#grad3)" opacity="0.1"/>
                <path d="M24 14C19 14 15 18 15 23C15 28 24 34 24 34C24 34 33 28 33 23C33 18 29 14 24 14Z" stroke="url(#grad3)" stroke-width="2" fill="none"/>
                <circle cx="24" cy="23" r="3" stroke="url(#grad3)" stroke-width="2" fill="none"/>
                <defs>
                  <linearGradient id="grad3" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#FFC107"/>
                    <stop offset="100%" style="stop-color:#FF9800"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h3>优越位置</h3>
            <p>城市中心，交通便利</p>
          </div>

          <div class="feature-card slide-in-left" style="animation-delay: 0.3s">
            <div class="feature-icon">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                <circle cx="24" cy="24" r="20" fill="url(#grad4)" opacity="0.1"/>
                <path d="M20 16H28C30 16 32 18 32 20V28C32 30 30 32 28 32H20C18 32 16 30 16 28V20C16 18 18 16 20 16Z" stroke="url(#grad4)" stroke-width="2" fill="none"/>
                <path d="M20 22H28M20 26H28" stroke="url(#grad4)" stroke-width="2" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="grad4" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#DC3545"/>
                    <stop offset="100%" style="stop-color:#C82333"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h3>豪华客房</h3>
            <p>舒适宽敞的居住空间</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 房间展示 -->
    <section class="rooms-showcase">
      <div class="container">
        <div class="section-header">
          <h2>精选房型</h2>
          <p>多种房型，满足您的不同需求</p>
        </div>

        <div class="rooms-carousel">
          <div class="room-card" v-for="room in featuredRooms" :key="room.id">
            <div class="room-image">
              <img :src="room.image" :alt="room.name" />
              <div class="room-badge">{{ room.discount }}% OFF</div>
            </div>
            <div class="room-content">
              <h3>{{ room.name }}</h3>
              <p class="room-desc">{{ room.description }}</p>
              <div class="room-features">
                <span v-for="feature in room.features" :key="feature" class="room-feature">
                  {{ feature }}
                </span>
              </div>
              <div class="room-footer">
                <div class="room-price">
                  <span class="price-current">¥{{ room.price }}</span>
                  <span class="price-original">¥{{ room.originalPrice }}</span>
                  <span class="price-unit">/晚</span>
                </div>
                <router-link :to="`/room-detail?id=${room.id}`" class="btn-room">
                  查看详情
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户评价 -->
    <section class="testimonials">
      <div class="container">
        <div class="section-header">
          <h2>客户评价</h2>
          <p>听听他们的声音</p>
        </div>

        <div class="testimonials-grid">
          <div class="testimonial-card" v-for="testimonial in testimonials" :key="testimonial.id">
            <div class="testimonial-rating">
              <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= testimonial.rating }">
                ★
              </span>
            </div>
            <p class="testimonial-text">{{ testimonial.text }}</p>
            <div class="testimonial-author">
              <div class="author-avatar">{{ testimonial.author.charAt(0) }}</div>
              <div class="author-info">
                <h4>{{ testimonial.author }}</h4>
                <span>{{ testimonial.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区域 -->
    <section class="cta-section">
      <div class="cta-content">
        <h2>准备好开启您的奢华之旅了吗？</h2>
        <p>立即预订，享受独家优惠</p>
        <router-link to="/rooms" class="btn-cta">
          开始预订
        </router-link>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="modern-footer">
      <div class="footer-container">
        <div class="footer-grid">
          <div class="footer-col">
            <h3>星悦酒店</h3>
            <p>为您提供舒适、优雅的居住体验</p>
            <div class="social-links">
              <a href="#" class="social-link">📘</a>
              <a href="#" class="social-link">📷</a>
              <a href="#" class="social-link">🐦</a>
            </div>
          </div>
          <div class="footer-col">
            <h4>快速链接</h4>
            <ul>
              <li><router-link to="/rooms">房间预订</router-link></li>
              <li><router-link to="/promotions">优惠活动</router-link></li>
              <li><router-link to="/contact">联系我们</router-link></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>联系方式</h4>
            <ul>
              <li>📍 北京市朝阳区建国路88号</li>
              <li>📞 010-8888-8888</li>
              <li>✉️ info@starhotel.com</li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>订阅优惠</h4>
            <p>获取最新优惠信息</p>
            <form class="subscribe-form">
              <input type="email" placeholder="您的邮箱" />
              <button type="submit">订阅</button>
            </form>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2024 星悦酒店. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import ModernNav from '@/components/ModernNav.vue'
import ChatBot from '@/components/ChatBot.vue'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const checkIn = ref('')
const checkOut = ref('')
const guests = ref('2')

// 精选房间
const featuredRooms = ref([
  {
    id: 1,
    name: '豪华海景房',
    description: '宽敞明亮，尽享海景',
    price: 588,
    originalPrice: 888,
    discount: 34,
    image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&h=300&fit=crop',
    features: ['海景', '大床', 'WiFi']
  },
  {
    id: 2,
    name: '商务套房',
    description: '商务出行的理想选择',
    price: 788,
    originalPrice: 1088,
    discount: 28,
    image: 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=400&h=300&fit=crop',
    features: ['办公区', '会议室', 'WiFi']
  },
  {
    id: 3,
    name: '家庭套房',
    description: '温馨舒适的家庭空间',
    price: 988,
    originalPrice: 1388,
    discount: 29,
    image: 'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400&h=300&fit=crop',
    features: ['双卧室', '厨房', '儿童区']
  }
])

// 客户评价
const testimonials = ref([
  {
    id: 1,
    text: '服务非常好，房间干净整洁，下次还会选择这里。',
    author: '张先生',
    rating: 5,
    date: '2024-01-15'
  },
  {
    id: 2,
    text: '位置方便，设施齐全，性价比很高。',
    author: '李女士',
    rating: 4,
    date: '2024-01-10'
  },
  {
    id: 3,
    text: '员工服务态度很好，早餐也很丰富。',
    author: '王先生',
    rating: 5,
    date: '2024-01-05'
  }
])

onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo()
})
</script>

<style scoped>
@import '@/styles/global.css';

.modern-home {
  min-height: 100vh;
}

/* 英雄区域 */
.hero-modern {
  position: relative;
  height: 100vh;
  min-height: 600px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
}

.hero-bg img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0,0,0,0.7), rgba(0,0,0,0.3));
}

.hero-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 4rem;
  align-items: center;
  padding-top: 80px;
}

.hero-text {
  color: white;
}

.hero-title {
  margin-bottom: 1.5rem;
}

.title-main {
  display: block;
  font-size: 3rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 0.5rem;
}

.title-sub {
  display: block;
  font-size: 1.5rem;
  font-weight: 300;
  opacity: 0.9;
}

.hero-desc {
  font-size: 1.125rem;
  opacity: 0.8;
  margin-bottom: 2rem;
}

.hero-cta {
  display: flex;
  gap: 1rem;
}

.btn-hero {
  padding: 0.875rem 2rem;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s;
}

.btn-primary-hero {
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  color: white;
  box-shadow: 0 4px 16px rgba(74, 144, 226, 0.3);
}

.btn-primary-hero:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(74, 144, 226, 0.4);
}

.btn-ghost-hero {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.btn-ghost-hero:hover {
  background: rgba(255, 255, 255, 0.2);
}

.btn-arrow {
  transition: transform 0.3s;
}

.btn-primary-hero:hover .btn-arrow {
  transform: translateX(4px);
}

/* 快速预订卡片 */
.booking-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.booking-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: var(--gray-900);
}

.booking-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.form-field label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--gray-600);
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}

.btn-submit {
  padding: 0.875rem;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
}

/* 特色服务 */
.features-section {
  padding: 5rem 0;
  background: var(--bg-secondary);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-header h2 {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--gray-900);
  margin-bottom: 1rem;
}

.section-header p {
  font-size: 1.125rem;
  color: var(--gray-600);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.feature-icon {
  display: inline-block;
  margin-bottom: 1.5rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--gray-900);
}

.feature-card p {
  color: var(--gray-600);
  font-size: 0.9375rem;
}

/* 房间展示 */
.rooms-showcase {
  padding: 5rem 0;
}

.rooms-carousel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
}

.room-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow-md);
  transition: all 0.3s;
}

.room-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.room-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.room-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.room-card:hover .room-image img {
  transform: scale(1.1);
}

.room-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: var(--danger-color);
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.room-content {
  padding: 1.5rem;
}

.room-content h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--gray-900);
}

.room-desc {
  color: var(--gray-600);
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

.room-features {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.room-feature {
  padding: 0.25rem 0.75rem;
  background: var(--gray-100);
  border-radius: 20px;
  font-size: 0.75rem;
  color: var(--gray-600);
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-price {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.price-current {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--primary-color);
}

.price-original {
  text-decoration: line-through;
  color: var(--gray-400);
  font-size: 0.875rem;
}

.price-unit {
  color: var(--gray-500);
  font-size: 0.875rem;
}

.btn-room {
  padding: 0.5rem 1rem;
  background: var(--primary-color);
  color: white;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-room:hover {
  background: var(--primary-hover);
  transform: translateX(2px);
}

/* 客户评价 */
.testimonials {
  padding: 5rem 0;
  background: var(--color-bg-secondary);
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.testimonial-card {
  background: var(--color-bg-primary);
  border-radius: 12px;
  padding: 2rem;
  box-shadow: var(--shadow-sm);
}

.testimonial-rating {
  margin-bottom: 1rem;
}

.star {
  color: var(--color-text-tertiary);
  font-size: 1.25rem;
}

.star.filled {
  color: #FFD700;
}

.testimonial-text {
  color: var(--color-text-primary);
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 1.125rem;
}

.author-info h4 {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.author-info span {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
}

/* CTA区域 */
.cta-section {
  padding: 5rem 0;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  text-align: center;
}

.cta-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
  color: white;
}

.cta-content h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.cta-content p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.btn-cta {
  display: inline-block;
  padding: 1rem 2.5rem;
  background: white;
  color: var(--primary-color);
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.125rem;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.3);
}

/* 页脚 */
.modern-footer {
  background: var(--gray-900);
  color: white;
  padding: 3rem 0 1rem;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.footer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-col h3 {
  margin-bottom: 1rem;
  font-size: 1.5rem;
  font-weight: 600;
}

.footer-col h4 {
  margin-bottom: 1rem;
  font-size: 1.125rem;
  font-weight: 500;
}

.footer-col p {
  color: var(--gray-400);
  margin-bottom: 1rem;
}

.footer-col ul {
  list-style: none;
}

.footer-col li {
  margin-bottom: 0.75rem;
  color: var(--gray-400);
}

.footer-col a {
  color: var(--gray-400);
  text-decoration: none;
  transition: color 0.2s;
}

.footer-col a:hover {
  color: white;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-link {
  display: inline-block;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  font-size: 1.25rem;
  transition: all 0.2s;
}

.social-link:hover {
  background: var(--primary-color);
  transform: translateY(-2px);
}

.subscribe-form {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

.subscribe-form input {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.subscribe-form input::placeholder {
  color: var(--gray-500);
}

.subscribe-form button {
  padding: 0.75rem 1.5rem;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.subscribe-form button:hover {
  background: var(--primary-hover);
}

.footer-bottom {
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  color: var(--gray-500);
}

/* 动画 */
.fade-in {
  animation: fadeIn 1s ease-out;
}

.fade-in-delay {
  animation: fadeIn 1s ease-out 0.3s both;
}

.slide-in-left {
  animation: slideInLeft 0.8s ease-out both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .booking-card {
    max-width: 500px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .title-main {
    font-size: 2rem;
  }

  .title-sub {
    font-size: 1.125rem;
  }

  .hero-cta {
    flex-direction: column;
  }

  .features-grid,
  .rooms-carousel,
  .testimonials-grid {
    grid-template-columns: 1fr;
  }

  .footer-grid {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .social-links {
    justify-content: center;
  }
}
</style>