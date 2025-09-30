<template>
  <nav class="modern-nav" :class="{ scrolled: isScrolled }">
    <div class="nav-container">
      <div class="nav-brand">
        <router-link to="/" class="brand-link">
          <span class="brand-icon">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <path d="M16 2L2 10V30H12V20H20V30H30V10L16 2Z" stroke="currentColor" stroke-width="2" fill="none"/>
              <path d="M12 14H14V16H12V14Z" fill="currentColor"/>
              <path d="M18 14H20V16H18V14Z" fill="currentColor"/>
            </svg>
          </span>
          <span class="brand-text">星悦酒店</span>
        </router-link>
      </div>

      <div class="nav-menu" :class="{ active: menuOpen }">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: $route.path === item.path }"
          @click="menuOpen = false"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-text">{{ item.text }}</span>
        </router-link>
      </div>

      <div class="nav-actions">
        <button class="nav-search hide-mobile">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="2"/>
            <path d="M14 14L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>

        <template v-if="!isLoggedIn">
          <router-link to="/login" class="nav-btn nav-btn-ghost">登录</router-link>
          <router-link to="/register" class="nav-btn nav-btn-primary">注册</router-link>
        </template>

        <template v-else>
          <div class="user-menu">
            <button class="user-trigger" @click="userMenuOpen = !userMenuOpen">
              <div class="user-avatar">{{ userInitial }}</div>
              <span class="user-name hide-mobile">{{ userName }}</span>
              <svg class="dropdown-icon" width="12" height="12" viewBox="0 0 12 12">
                <path d="M2 4L6 8L10 4" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round"/>
              </svg>
            </button>

            <transition name="dropdown">
              <div v-if="userMenuOpen" class="user-dropdown">
                <router-link to="/profile" class="dropdown-item" @click="userMenuOpen = false">
                  <span>👤</span> 个人中心
                </router-link>
                <router-link to="/my-bookings" class="dropdown-item" @click="userMenuOpen = false">
                  <span>📋</span> 我的预订
                </router-link>
                <router-link v-if="isAdmin" to="/admin" class="dropdown-item" @click="userMenuOpen = false">
                  <span>⚙️</span> 管理后台
                </router-link>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item logout" @click="handleLogout">
                  <span>🚪</span> 退出登录
                </button>
              </div>
            </transition>
          </div>
        </template>

        <button class="menu-toggle hide-desktop" @click="menuOpen = !menuOpen">
          <span class="hamburger" :class="{ active: menuOpen }">
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isScrolled = ref(false)
const menuOpen = ref(false)
const userMenuOpen = ref(false)

const menuItems = [
  { path: '/', text: '首页', icon: '🏠' },
  { path: '/rooms', text: '房间预订', icon: '🛏️' },
  { path: '/promotions', text: '优惠活动', icon: '🎁' },
  { path: '/contact', text: '联系我们', icon: '📞' }
]

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)
const userName = computed(() => userInfo.value?.realName || userInfo.value?.name || userInfo.value?.username || '用户')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const handleClickOutside = (event) => {
  if (!event.target.closest('.user-menu')) {
    userMenuOpen.value = false
  }
  if (!event.target.closest('.modern-nav')) {
    menuOpen.value = false
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
  userMenuOpen.value = false
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleClickOutside)
  userStore.initUserInfo()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.modern-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  z-index: 1000;
  transition: all 0.3s ease;
}

.modern-nav.scrolled {
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  padding: 0 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Brand */
.nav-brand {
  flex-shrink: 0;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  color: var(--gray-900);
  font-weight: 600;
  font-size: 1.25rem;
  transition: opacity 0.2s;
}

.brand-link:hover {
  opacity: 0.8;
}

.brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border-radius: 10px;
  color: white;
}

.brand-text {
  letter-spacing: -0.5px;
}

/* Menu */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  justify-content: center;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: var(--gray-600);
  font-weight: 500;
  font-size: 0.9375rem;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-item:hover {
  color: var(--primary-color);
  background: var(--primary-light);
}

.nav-item.active {
  color: var(--primary-color);
  background: var(--primary-light);
}

.nav-icon {
  font-size: 1.125rem;
}

/* Actions */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: var(--gray-100);
  border-radius: 10px;
  color: var(--gray-600);
  cursor: pointer;
  transition: all 0.2s;
}

.nav-search:hover {
  background: var(--gray-200);
  color: var(--gray-900);
}

.nav-btn {
  padding: 0.5rem 1.25rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.nav-btn-ghost {
  color: var(--gray-700);
  background: transparent;
}

.nav-btn-ghost:hover {
  background: var(--gray-100);
}

.nav-btn-primary {
  color: white;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  box-shadow: 0 2px 8px rgba(74, 144, 226, 0.3);
}

.nav-btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.4);
}

/* User Menu */
.user-menu {
  position: relative;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.375rem 0.75rem;
  background: var(--gray-100);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-trigger:hover {
  background: var(--gray-200);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 0.875rem;
}

.user-name {
  color: var(--gray-700);
  font-weight: 500;
  font-size: 0.875rem;
}

.dropdown-icon {
  transition: transform 0.2s;
}

.user-trigger:hover .dropdown-icon {
  transform: translateY(1px);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  min-width: 200px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: var(--gray-700);
  text-decoration: none;
  font-size: 0.875rem;
  transition: all 0.2s;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.dropdown-item:hover {
  background: var(--gray-50);
  color: var(--primary-color);
}

.dropdown-item.logout {
  color: var(--danger-color);
}

.dropdown-divider {
  height: 1px;
  background: var(--gray-200);
  margin: 0.25rem 0;
}

/* Mobile Menu Toggle */
.menu-toggle {
  display: none;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
}

.hamburger {
  position: relative;
  display: block;
  width: 24px;
  height: 24px;
}

.hamburger span {
  position: absolute;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--gray-700);
  border-radius: 2px;
  transition: all 0.3s;
}

.hamburger span:nth-child(1) {
  top: 4px;
}

.hamburger span:nth-child(2) {
  top: 11px;
}

.hamburger span:nth-child(3) {
  top: 18px;
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg);
  top: 11px;
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
}

.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg);
  top: 11px;
}

/* Transitions */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive */
@media (max-width: 768px) {
  .hide-mobile {
    display: none !important;
  }

  .nav-menu {
    position: fixed;
    top: 64px;
    left: 0;
    right: 0;
    background: white;
    flex-direction: column;
    padding: 1rem;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateX(-100%);
    transition: transform 0.3s;
  }

  .nav-menu.active {
    transform: translateX(0);
  }

  .nav-item {
    width: 100%;
    justify-content: flex-start;
  }

  .menu-toggle {
    display: block;
  }
}

@media (min-width: 769px) {
  .hide-desktop {
    display: none !important;
  }
}
</style>