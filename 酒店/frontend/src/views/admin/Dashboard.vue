<template>
  <div class="dashboard">
    <h2>仪表盘</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <h3>总房间数</h3>
              <p class="stat-number">{{ stats.totalRooms }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon><Select /></el-icon>
            </div>
            <div class="stat-info">
              <h3>可用房间</h3>
              <p class="stat-number">{{ stats.availableRooms }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <h3>今日预订</h3>
              <p class="stat-number">{{ stats.todayReservations }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>客户总数</h3>
              <p class="stat-number">{{ stats.totalCustomers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表和列表 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近预订</span>
          </template>
          <el-table :data="recentReservations" style="width: 100%">
            <el-table-column prop="id" label="订单号" width="80" />
            <el-table-column prop="customerName" label="客户姓名" />
            <el-table-column prop="roomNumber" label="房间号" />
            <el-table-column prop="checkIn" label="入住时间" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag
                  :type="scope.row.status === '已确认' ? 'success' : 'warning'"
                >
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>房间状态分布</span>
          </template>
          <div class="room-status-chart">
            <div class="status-item">
              <div class="status-color available"></div>
              <span>可用房间: {{ stats.availableRooms }}</span>
            </div>
            <div class="status-item">
              <div class="status-color occupied"></div>
              <span>已入住: {{ stats.occupiedRooms }}</span>
            </div>
            <div class="status-item">
              <div class="status-color maintenance"></div>
              <span>维护中: {{ stats.maintenanceRooms }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { House, Select, Calendar, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const stats = ref({
  totalRooms: 0,
  availableRooms: 0,
  occupiedRooms: 0,
  maintenanceRooms: 0,
  todayReservations: 0,
  totalCustomers: 0
})

const recentReservations = ref([])
const loading = ref(false)

// 获取仪表盘统计数据
const fetchDashboardStats = async () => {
  try {
    loading.value = true
    const response = await api.get('/dashboard/statistics')
    
    const data = response.data
    stats.value = {
      totalRooms: data.rooms.total || 0,
      availableRooms: data.rooms.available || 0,
      occupiedRooms: data.rooms.occupied || 0,
      maintenanceRooms: data.rooms.maintenance || 0,
      todayReservations: data.reservations.today || 0,
      totalCustomers: data.customers.total || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 获取最近预订
const fetchRecentReservations = async () => {
  try {
    const response = await api.get('/dashboard/recent-reservations')
    recentReservations.value = response.data.map(item => ({
      id: item.orderId,
      customerName: item.customer?.name || '未知',
      roomNumber: item.room?.roomNumber || '未知',
      checkIn: item.checkInDate,
      status: getStatusText(item.status)
    }))
  } catch (error) {
    console.error('获取最近预订失败:', error)
    ElMessage.error('获取最近预订失败')
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'CHECKED_IN': '已入住',
    'CHECKED_OUT': '已退房',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}

onMounted(async () => {
  await Promise.all([
    fetchDashboardStats(),
    fetchRecentReservations()
  ])
})
</script>

<style scoped>
.dashboard {
  padding: 0;
  animation: slideUp 0.6s ease-out;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dashboard h2 {
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  flex-shrink: 0;
}

.stats-row {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.stat-card {
  cursor: pointer;
  transition: all var(--transition-base);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: var(--color-bg-primary);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1.25rem;
  background: var(--color-bg-secondary);
  position: relative;
}

.stat-icon .el-icon {
  font-size: 1.75rem;
  color: var(--color-text-primary);
  opacity: 0.7;
}

.stat-info {
  flex: 1;
}

.stat-info h3 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-number {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1;
  letter-spacing: -0.02em;
}

.charts-row {
  margin-top: 0;
  flex: 1;
  min-height: 0;
}

.charts-row .el-col {
  height: 100%;
}

.charts-row .el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.charts-row .el-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 表格容器自适应 */
.charts-row .el-card :deep(.el-table) {
  flex: 1;
  min-height: 0;
}

.charts-row .el-card :deep(.el-table .el-table__body-wrapper) {
  max-height: calc(100vh - 350px);
  overflow-y: auto;
}

.charts-row .el-card {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  background: var(--color-bg-primary);
  overflow: hidden;
  transition: all var(--transition-base);
}

.charts-row .el-card:hover {
  box-shadow: var(--shadow-md);
}

:deep(.el-card__header) {
  background: var(--color-bg-primary);
  color: var(--color-text-primary);
  font-weight: 600;
  font-size: 1rem;
  border-bottom: 1px solid var(--color-border);
  padding: 1.25rem;
}

:deep(.el-card__body) {
  padding: 24px;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
  border: none;
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
  transition: all var(--transition-fast);
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-table tbody tr:hover) {
  background: var(--color-bg-secondary);
}

/* 标签样式优化 */
:deep(.el-tag--success) {
  background: rgba(52, 199, 89, 0.1);
  border: none;
  color: var(--color-success);
  font-weight: 500;
  border-radius: var(--radius-xl);
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

:deep(.el-tag--warning) {
  background: rgba(255, 149, 0, 0.1);
  border: none;
  color: var(--color-warning);
  font-weight: 500;
  border-radius: var(--radius-xl);
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

.room-status-chart {
  padding: 20px 0;
}

.status-item {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  padding: 1rem;
  border-radius: var(--radius-md);
  background: var(--color-bg-secondary);
  transition: all var(--transition-fast);
  font-weight: 500;
  color: var(--color-text-primary);
  font-size: 0.9375rem;
}

.status-item:hover {
  background: var(--color-bg-tertiary);
}

.status-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 0.75rem;
}

.status-color.available {
  background: var(--color-success);
}

.status-color.occupied {
  background: var(--color-warning);
}

.status-color.maintenance {
  background: var(--color-danger);
}

/* 简化动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dashboard {
  animation: fadeIn 0.3s ease-out;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard h2 {
    font-size: 28px;
  }
  
  .stat-icon {
    width: 70px;
    height: 70px;
  }
  
  .stat-icon .el-icon {
    font-size: 28px;
  }
  
  .stat-number {
    font-size: 32px;
  }
}

@media (max-width: 768px) {
  .dashboard h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-content {
    padding: 20px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    margin-right: 15px;
  }
  
  .stat-icon .el-icon {
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .charts-row {
    margin-top: 20px;
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
  
  .room-status-chart {
    padding: 15px 0;
  }
  
  .status-item {
    padding: 12px;
    margin-bottom: 15px;
  }
}

@media (max-width: 480px) {
  .dashboard h2 {
    font-size: 20px;
    text-align: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
    padding: 15px;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  :deep(.el-card__header) {
    padding: 15px;
    font-size: 14px;
  }
  
  :deep(.el-card__body) {
    padding: 15px;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  .status-item {
    font-size: 14px;
  }
}
</style>