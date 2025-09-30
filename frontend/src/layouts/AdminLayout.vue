<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="250px" class="sidebar">
        <div class="logo">
          <h3>酒店管理系统</h3>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          router
        >
          <el-menu-item index="/admin">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/rooms">
            <el-icon><House /></el-icon>
            <span>客房管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/reservations">
            <el-icon><Calendar /></el-icon>
            <span>预订管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/customers">
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <div class="breadcrumb">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item>管理后台</el-breadcrumb-item>
                <el-breadcrumb-item>{{ getBreadcrumbTitle() }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            
            <div class="user-menu">
              <el-dropdown @command="handleCommand">
                <span class="user-info">
                  <el-icon><Avatar /></el-icon>
                  {{ currentUser.username }}
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Odometer,
  House,
  Calendar,
  User,
  Document,
  Avatar,
  ArrowDown,
  Bell
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const currentUser = ref({ username: 'admin' })

onMounted(() => {
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
  }
})

const getBreadcrumbTitle = () => {
  const routeMap = {
    '/admin': '仪表盘',
    '/admin/rooms': '客房管理',
    '/admin/reservations': '预订管理',
    '/admin/customers': '客户管理',
    '/admin/orders': '订单管理',
    '/admin/announcements': '公告管理'
  }
  return routeMap[route.path] || '未知页面'
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消操作
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
  background: var(--color-bg-secondary);
  overflow: hidden;
}

.sidebar {
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: var(--color-text-primary);
  box-shadow: var(--shadow-md);
  border-right: 1px solid var(--color-border);
  flex-shrink: 0;
  width: 250px !important;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--color-border);
  position: relative;
}

.logo h3 {
  color: var(--color-text-primary);
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 20px 0;
}

.sidebar-menu .el-menu-item {
  color: var(--color-text-secondary);
  border: none;
  margin: 0.25rem 0.75rem;
  border-radius: var(--radius-md);
  font-weight: 500;
  font-size: 0.9375rem;
  transition: all var(--transition-fast);
  position: relative;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-menu-item:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

.sidebar-menu .el-menu-item.is-active {
  background: var(--color-primary);
  color: white;
  font-weight: 600;
}

.sidebar-menu .el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 12px;
}

.header {
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border);
  padding: 0 1.5rem;
  height: 60px;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.breadcrumb {
  font-weight: 500;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--color-text-primary);
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-xl);
  transition: all var(--transition-fast);
  font-size: 0.875rem;
}

.user-info:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

.user-info .el-icon {
  margin: 0 5px;
  font-size: 16px;
}

.main-content {
  background: var(--color-bg-secondary);
  padding: 1.5rem;
  position: relative;
  height: calc(100vh - 60px);
  overflow-y: auto;
  overflow-x: hidden;
  flex: 1;
  max-width: none;
}

/* 全局卡片样式优化 */
:deep(.el-card) {
  border: 1px solid var(--color-border);
  background: var(--color-bg-primary);
  box-shadow: var(--shadow-sm);
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
  width: 100%;
  margin: 0;
}

:deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
}

:deep(.el-card__header) {
  background: var(--color-bg-primary);
  color: var(--color-text-primary);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  border-bottom: 1px solid var(--color-border);
  font-weight: 600;
  padding: 1.25rem;
}

/* 按钮样式优化 */
:deep(.el-button--primary) {
  background: var(--color-primary);
  border: none;
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

:deep(.el-button--primary:hover) {
  background: var(--color-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

:deep(.el-button--default) {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-button--default:hover) {
  background: var(--color-bg-secondary);
  border-color: var(--color-separator);
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--color-bg-primary);
  box-shadow: none;
  width: 100%;
}

:deep(.el-table th) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-table tbody tr) {
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-table tbody tr:hover) {
  background: var(--color-bg-secondary);
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: var(--radius-xl);
  font-weight: 500;
  border: none;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

:deep(.el-tag--success) {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

:deep(.el-tag--warning) {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

:deep(.el-tag--danger) {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

:deep(.el-tag--info) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

/* 分页样式优化 */
:deep(.el-pagination) {
  justify-content: center;
  padding: 1rem 0;
}

:deep(.el-pagination .el-pager li) {
  border-radius: var(--radius-sm);
  margin: 0 0.25rem;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  transition: all var(--transition-fast);
  font-weight: 500;
}

:deep(.el-pagination .el-pager li:hover) {
  background: var(--color-bg-secondary);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

:deep(.el-pagination .el-pager li.is-active) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  background: var(--color-bg-primary);
}

:deep(.el-dialog__header) {
  background: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border);
  padding: 1.5rem;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 1.125rem;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: var(--color-text-secondary);
  font-size: 1.25rem;
  transition: color var(--transition-fast);
}

:deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: var(--color-text-primary);
}

:deep(.el-dialog__body) {
  padding: 1.5rem;
}

/* 表单样式优化 */
:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: var(--color-bg-primary);
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

:deep(.el-select .el-input.is-focus .el-input__wrapper) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

:deep(.el-form-item__label) {
  color: var(--color-text-secondary);
  font-weight: 500;
  font-size: 0.875rem;
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border);
  background: var(--color-bg-primary);
  padding: 0.5rem;
}

:deep(.el-dropdown-menu__item) {
  border-radius: var(--radius-sm);
  padding: 0.5rem 1rem;
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
  font-size: 0.875rem;
}

:deep(.el-dropdown-menu__item:hover) {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

/* 简化动画效果 */
.sidebar {
  animation: fadeIn 0.3s ease-out;
}

.main-content {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 响应式设计 */
/* 大屏幕优化 */
@media (min-width: 1920px) {
  .sidebar {
    width: 280px !important;
  }
  
  .logo h3 {
    font-size: 20px;
  }
  
  .sidebar-menu .el-menu-item {
    font-size: 16px;
    padding: 0 24px;
    height: 56px;
    line-height: 56px;
  }
  
  .main-content {
    padding: 24px;
  }
}

@media (max-width: 1200px) {
  .sidebar {
    width: 200px !important;
  }
  
  .logo h3 {
    font-size: 16px;
  }
  
  .sidebar-menu .el-menu-item {
    margin: 6px 12px;
    font-size: 14px;
  }
  
  .main-content {
    padding: 16px;
  }
}

/* 中等屏幕 */
@media (max-width: 992px) {
  .sidebar {
    width: 180px !important;
  }
  
  .sidebar-menu .el-menu-item span {
    font-size: 13px;
  }
  
  .header {
    padding: 0 16px;
  }
}

/* 平板端 */
@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100% !important;
    height: auto;
    position: relative;
    order: 2;
  }
  
  .sidebar-menu {
    display: flex;
    overflow-x: auto;
    padding: 8px;
    margin: 0;
  }
  
  .sidebar-menu .el-menu-item {
    white-space: nowrap;
    margin: 0 6px;
    min-width: 120px;
    border-radius: 8px;
  }
  
  .header {
    padding: 0 15px;
    order: 1;
  }
  
  .header-content {
    flex-direction: row;
    gap: 0;
    align-items: center;
  }
  
  .main-content {
    padding: 12px;
    height: calc(100vh - 140px);
    order: 3;
  }
  
  /* 表格在平板端的优化 */
  :deep(.el-table) {
    font-size: 13px;
    min-height: calc(100vh - 320px);
  }
  
  :deep(.el-table .el-table__body-wrapper) {
    max-height: calc(100vh - 380px);
  }
}

/* 手机端 */
@media (max-width: 480px) {
  .logo h3 {
    font-size: 14px;
  }
  
  .main-content {
    padding: 8px;
    height: calc(100vh - 140px);
  }
  
  .sidebar-menu .el-menu-item {
    min-width: 90px;
    font-size: 12px;
    padding: 0 8px;
  }
  
  .sidebar-menu .el-menu-item .el-icon {
    font-size: 16px;
    margin-right: 8px;
  }
  
  .header {
    padding: 0 10px;
  }
  
  .user-info {
    padding: 6px 12px;
    font-size: 14px;
  }
  
  /* 手机端表格优化 */
  :deep(.el-table) {
    font-size: 12px;
    min-height: calc(100vh - 300px);
  }
  
  :deep(.el-table .el-table__body-wrapper) {
    max-height: calc(100vh - 360px);
  }
  
  :deep(.el-table th) {
    padding: 8px 4px;
  }
  
  :deep(.el-table td) {
    padding: 8px 4px;
  }
  
  /* 按钮组在手机端的优化 */
  :deep(.el-button--small) {
    padding: 4px 8px;
    font-size: 11px;
  }
  
  /* 对话框在手机端的优化 */
  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
  }
  
  /* 表单在手机端的优化 */
  :deep(.el-form--inline) {
    display: block;
  }
  
  :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 12px;
  }
}
</style>