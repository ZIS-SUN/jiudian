<template>
  <div class="promotions-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h2>🏨 星级酒店</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/rooms" class="nav-link">房间预订</router-link>
          <router-link to="/promotions" class="nav-link active">优惠活动</router-link>
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

    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>专属优惠</h1>
        <p>尊享特惠价格，体验品质服务</p>
      </div>
    </section>

    <div class="page-content">
      <div class="container">
        <!-- 优惠券区域 -->
        <section class="coupons-section">
          <h2>限时优惠券</h2>
          <div class="coupons-grid">
            <div
              v-for="coupon in coupons"
              :key="coupon.id"
              class="coupon-card"
              :class="{ claimed: coupon.claimed, expired: coupon.expired }"
            >
              <div class="coupon-left">
                <div class="discount-amount">
                  <span v-if="coupon.type === 'percentage'" class="amount">{{ coupon.discount }}%</span>
                  <span v-else class="amount">¥{{ coupon.discount }}</span>
                  <span class="label">{{ coupon.type === 'percentage' ? 'OFF' : '优惠' }}</span>
                </div>
              </div>
              <div class="coupon-middle">
                <h3>{{ coupon.title }}</h3>
                <p>{{ coupon.description }}</p>
                <div class="coupon-meta">
                  <span class="min-spend">满{{ coupon.minSpend }}元可用</span>
                  <span class="validity">有效期至：{{ formatDate(coupon.validUntil) }}</span>
                </div>
              </div>
              <div class="coupon-right">
                <el-button
                  v-if="!coupon.claimed && !coupon.expired"
                  type="primary"
                  size="small"
                  @click="claimCoupon(coupon)"
                >
                  立即领取
                </el-button>
                <el-button v-else-if="coupon.claimed" disabled size="small">
                  已领取
                </el-button>
                <el-button v-else disabled size="small">
                  已过期
                </el-button>
                <div class="coupon-code" v-if="coupon.claimed">
                  代码: {{ coupon.code }}
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 特价房型 -->
        <section class="deals-section">
          <h2>特价房型</h2>
          <div class="deals-grid">
            <div v-for="deal in specialDeals" :key="deal.id" class="deal-card">
              <div class="deal-badge">{{ deal.discount }}% OFF</div>
              <div class="deal-image">
                <img :src="deal.image" :alt="deal.roomType" />
              </div>
              <div class="deal-content">
                <h3>{{ deal.roomType }}</h3>
                <p class="deal-description">{{ deal.description }}</p>
                <div class="price-info">
                  <span class="original-price">¥{{ deal.originalPrice }}</span>
                  <span class="deal-price">¥{{ deal.dealPrice }}</span>
                  <span class="per-night">/晚</span>
                </div>
                <div class="deal-validity">
                  <i class="el-icon-time"></i>
                  {{ formatDateRange(deal.validFrom, deal.validTo) }}
                </div>
                <el-button type="primary" @click="bookDeal(deal)">立即预订</el-button>
              </div>
            </div>
          </div>
        </section>

        <!-- 会员专享 -->
        <section class="membership-section">
          <h2>会员专享福利</h2>
          <div class="membership-benefits">
            <div class="benefit-card gold">
              <div class="benefit-header">
                <span class="tier">金卡会员</span>
                <span class="discount">8.8折</span>
              </div>
              <ul class="benefit-list">
                <li>房费8.8折优惠</li>
                <li>免费早餐</li>
                <li>延迟退房至14:00</li>
                <li>生日专属礼遇</li>
                <li>积分双倍累积</li>
              </ul>
              <el-button @click="upgradeMembership('gold')">升级金卡</el-button>
            </div>

            <div class="benefit-card platinum">
              <div class="benefit-header">
                <span class="tier">铂金卡会员</span>
                <span class="discount">7.8折</span>
              </div>
              <ul class="benefit-list">
                <li>房费7.8折优惠</li>
                <li>免费早晚餐</li>
                <li>免费房型升级</li>
                <li>机场接送服务</li>
                <li>专属客服经理</li>
                <li>积分三倍累积</li>
              </ul>
              <el-button type="primary" @click="upgradeMembership('platinum')">升级铂金卡</el-button>
            </div>

            <div class="benefit-card diamond">
              <div class="benefit-header">
                <span class="tier">钻石卡会员</span>
                <span class="discount">6.8折</span>
              </div>
              <ul class="benefit-list">
                <li>房费6.8折优惠</li>
                <li>全天候餐饮服务</li>
                <li>保证房型升级</li>
                <li>24小时管家服务</li>
                <li>行政酒廊权限</li>
                <li>积分五倍累积</li>
                <li>年度专属礼品</li>
              </ul>
              <el-button type="warning" @click="upgradeMembership('diamond')">升级钻石卡</el-button>
            </div>
          </div>
        </section>

        <!-- 积分兑换 -->
        <section class="points-section">
          <h2>积分兑换</h2>
          <div class="points-header">
            <div class="my-points" v-if="isLoggedIn">
              <span class="label">我的积分：</span>
              <span class="points">{{ userPoints }}</span>
            </div>
            <el-button v-else type="primary" @click="goToLogin">登录查看积分</el-button>
          </div>
          <div class="rewards-grid">
            <div v-for="reward in rewards" :key="reward.id" class="reward-card">
              <div class="reward-image">
                <img :src="reward.image" :alt="reward.name" />
              </div>
              <div class="reward-info">
                <h4>{{ reward.name }}</h4>
                <p>{{ reward.description }}</p>
                <div class="points-required">
                  <span class="points">{{ reward.points }}</span>
                  <span class="label">积分</span>
                </div>
                <el-button
                  size="small"
                  :disabled="!canRedeem(reward)"
                  @click="redeemReward(reward)"
                >
                  {{ canRedeem(reward) ? '立即兑换' : '积分不足' }}
                </el-button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 用户状态
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)
const userPoints = ref(2580)

// 优惠券数据
const coupons = ref([
  {
    id: 1,
    title: '新用户专享',
    description: '首次预订立减100元',
    type: 'fixed',
    discount: 100,
    minSpend: 500,
    validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
    code: 'NEW100',
    claimed: false,
    expired: false
  },
  {
    id: 2,
    title: '周末特惠',
    description: '周五至周日入住享8折优惠',
    type: 'percentage',
    discount: 20,
    minSpend: 300,
    validUntil: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
    code: 'WEEK20',
    claimed: false,
    expired: false
  },
  {
    id: 3,
    title: '连住优惠',
    description: '连续入住3晚及以上享9折',
    type: 'percentage',
    discount: 10,
    minSpend: 800,
    validUntil: new Date(Date.now() + 60 * 24 * 60 * 60 * 1000),
    code: 'STAY10',
    claimed: false,
    expired: false
  }
])

// 特价房型
const specialDeals = ref([
  {
    id: 1,
    roomType: '豪华海景房',
    description: '面朝大海，春暖花开',
    originalPrice: 688,
    dealPrice: 488,
    discount: 30,
    image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&h=250&fit=crop',
    validFrom: new Date(),
    validTo: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000)
  },
  {
    id: 2,
    roomType: '行政套房',
    description: '商务出行首选',
    originalPrice: 1288,
    dealPrice: 888,
    discount: 31,
    image: 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=400&h=250&fit=crop',
    validFrom: new Date(),
    validTo: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
  },
  {
    id: 3,
    roomType: '家庭亲子房',
    description: '温馨舒适，适合家庭',
    originalPrice: 588,
    dealPrice: 388,
    discount: 34,
    image: 'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400&h=250&fit=crop',
    validFrom: new Date(),
    validTo: new Date(Date.now() + 21 * 24 * 60 * 60 * 1000)
  }
])

// 积分兑换奖励
const rewards = ref([
  {
    id: 1,
    name: '免费早餐券',
    description: '单人自助早餐一份',
    points: 500,
    image: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=200&h=150&fit=crop'
  },
  {
    id: 2,
    name: '50元房费抵用券',
    description: '预订时可抵扣50元',
    points: 1000,
    image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=200&h=150&fit=crop'
  },
  {
    id: 3,
    name: '免费房型升级',
    description: '下次入住免费升级房型',
    points: 2000,
    image: 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=200&h=150&fit=crop'
  },
  {
    id: 4,
    name: '免费住宿一晚',
    description: '标准间免费住宿一晚',
    points: 5000,
    image: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=200&h=150&fit=crop'
  }
])

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateRange = (from, to) => {
  return `${formatDate(from)} - ${formatDate(to)}`
}

const claimCoupon = (coupon) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  coupon.claimed = true
  ElMessage.success(`成功领取优惠券：${coupon.title}`)

  // 保存到localStorage
  const claimedCoupons = JSON.parse(localStorage.getItem('claimedCoupons') || '[]')
  claimedCoupons.push({
    ...coupon,
    claimedAt: new Date().toISOString(),
    userId: userInfo.value?.id
  })
  localStorage.setItem('claimedCoupons', JSON.stringify(claimedCoupons))
}

const bookDeal = (deal) => {
  router.push({
    path: '/booking',
    query: {
      roomType: deal.roomType,
      price: deal.dealPrice,
      dealId: deal.id
    }
  })
}

const upgradeMembership = (tier) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  ElMessage.info(`会员升级功能即将推出`)
}

const goToLogin = () => {
  router.push('/login')
}

const canRedeem = (reward) => {
  return isLoggedIn.value && userPoints.value >= reward.points
}

const redeemReward = (reward) => {
  if (!canRedeem(reward)) return

  userPoints.value -= reward.points
  ElMessage.success(`成功兑换：${reward.name}`)

  // 保存兑换记录
  const redemptions = JSON.parse(localStorage.getItem('pointRedemptions') || '[]')
  redemptions.push({
    ...reward,
    redeemedAt: new Date().toISOString(),
    userId: userInfo.value?.id
  })
  localStorage.setItem('pointRedemptions', JSON.stringify(redemptions))
}

// 加载用户的优惠券状态
const loadUserCoupons = () => {
  if (!isLoggedIn.value) return

  const claimedCoupons = JSON.parse(localStorage.getItem('claimedCoupons') || '[]')
  const userCoupons = claimedCoupons.filter(c => c.userId === userInfo.value?.id)

  coupons.value.forEach(coupon => {
    const claimed = userCoupons.find(c => c.id === coupon.id)
    if (claimed) {
      coupon.claimed = true
    }

    // 检查是否过期
    if (new Date(coupon.validUntil) < new Date()) {
      coupon.expired = true
    }
  })
}

onMounted(() => {
  userStore.initUserInfo()
  loadUserCoupons()
})
</script>

<style scoped>
.promotions-page {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

/* 导航栏样式 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
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
  opacity: 0.8;
}

.nav-link:hover,
.nav-link.active {
  color: var(--color-primary);
  opacity: 1;
}

.user-login {
  background: var(--color-primary) !important;
  color: white !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  opacity: 1 !important;
}

.user-register {
  background: transparent !important;
  color: var(--color-primary) !important;
  border: 1px solid var(--color-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  opacity: 1 !important;
}

.user-info {
  color: var(--color-text-primary);
  font-weight: 500;
  font-size: 0.875rem;
}

.logout-btn {
  background: transparent;
  color: var(--color-danger);
  border: 1px solid var(--color-danger);
  padding: 0.5rem 1.25rem;
  border-radius: var(--radius-xl);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.logout-btn:hover {
  background: var(--color-danger);
  color: white;
}

.admin-link {
  background: var(--color-text-primary) !important;
  color: var(--color-bg-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  opacity: 1 !important;
}

/* 英雄区域 */
.hero-section {
  height: 300px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 76px;
}

.hero-content {
  text-align: center;
  color: white;
}

.hero-content h1 {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.125rem;
}

/* 页面内容 */
.page-content {
  padding: 3rem 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

section {
  margin-bottom: 4rem;
}

section h2 {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: var(--color-text-primary);
}

/* 优惠券样式 */
.coupons-grid {
  display: grid;
  gap: 1.5rem;
}

.coupon-card {
  display: flex;
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 2px dashed var(--color-primary);
  transition: all 0.3s;
}

.coupon-card.claimed {
  opacity: 0.7;
  border-color: var(--color-border);
}

.coupon-card.expired {
  opacity: 0.5;
  border-color: var(--color-border);
}

.coupon-left {
  background: var(--color-primary);
  color: white;
  padding: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 150px;
}

.discount-amount {
  text-align: center;
}

.discount-amount .amount {
  display: block;
  font-size: 2rem;
  font-weight: 600;
}

.discount-amount .label {
  font-size: 0.875rem;
  opacity: 0.9;
}

.coupon-middle {
  flex: 1;
  padding: 1.5rem;
}

.coupon-middle h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--color-text-primary);
}

.coupon-middle p {
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
}

.coupon-meta {
  display: flex;
  gap: 2rem;
  font-size: 0.875rem;
  color: var(--color-text-tertiary);
}

.coupon-right {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}

.coupon-code {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-primary);
}

/* 特价房型 */
.deals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
}

.deal-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  position: relative;
}

.deal-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: var(--color-danger);
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-xl);
  font-weight: 600;
  font-size: 0.875rem;
  z-index: 10;
}

.deal-image {
  height: 200px;
  overflow: hidden;
}

.deal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.deal-content {
  padding: 1.5rem;
}

.deal-content h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--color-text-primary);
}

.deal-description {
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
  font-size: 0.875rem;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.original-price {
  text-decoration: line-through;
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
}

.deal-price {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-danger);
}

.per-night {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.deal-validity {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

/* 会员专享 */
.membership-benefits {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.benefit-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  transition: transform 0.3s;
}

.benefit-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.benefit-card.gold {
  border-top: 4px solid #ffd700;
}

.benefit-card.platinum {
  border-top: 4px solid #e5e4e2;
}

.benefit-card.diamond {
  border-top: 4px solid #b9f2ff;
}

.benefit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.tier {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.benefit-header .discount {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-primary);
}

.benefit-list {
  list-style: none;
  padding: 0;
  margin: 0 0 1.5rem 0;
}

.benefit-list li {
  padding: 0.5rem 0;
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
  position: relative;
  padding-left: 1.5rem;
}

.benefit-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--color-success);
  font-weight: bold;
}

/* 积分兑换 */
.points-section {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.my-points {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.my-points .label {
  color: var(--color-text-secondary);
  font-size: 1rem;
}

.my-points .points {
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-primary);
}

.rewards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.reward-card {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--color-border);
  transition: transform 0.2s;
}

.reward-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.reward-image {
  height: 120px;
  overflow: hidden;
}

.reward-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reward-info {
  padding: 1rem;
}

.reward-info h4 {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--color-text-primary);
}

.reward-info p {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin-bottom: 1rem;
  line-height: 1.4;
}

.points-required {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
  margin-bottom: 1rem;
}

.points-required .points {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-primary);
}

.points-required .label {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .hero-content h1 {
    font-size: 2rem;
  }

  .coupon-card {
    flex-direction: column;
  }

  .coupon-left {
    min-width: auto;
    padding: 1.5rem;
  }

  .deals-grid,
  .membership-benefits {
    grid-template-columns: 1fr;
  }

  .rewards-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>