<template>
  <div class="reservations">
    <div class="page-header">
      <h2>预订管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新增预订
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderId" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型">
            <el-option label="全部" value="" />
            <el-option label="酒店预订" value="hotel" />
            <el-option label="景点门票" value="attraction" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已入住" value="已入住" />
            <el-option label="已退房" value="已退房" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="exportData">导出数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 预订列表 -->
    <el-card>
      <el-table :data="reservations" :loading="loading" style="width: 100%">
        <el-table-column prop="orderId" label="订单号" width="150" />
        <el-table-column label="类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'hotel' ? 'primary' : 'warning'" size="small">
              {{ scope.row.type === 'hotel' ? '酒店' : '景点' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户姓名" width="120">
          <template #default="scope">
            {{ scope.row.customer?.name || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" width="120">
          <template #default="scope">
            {{ scope.row.customer?.phone || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="预订内容" min-width="150">
          <template #default="scope">
            <div v-if="scope.row.type === 'hotel'">
              <div>{{ scope.row.room?.roomType?.typeName || '房型信息' }}</div>
              <small class="text-secondary">房间号: {{ scope.row.room?.roomNumber || '待分配' }}</small>
            </div>
            <div v-else>
              <div>{{ scope.row.attraction?.name || '景点信息' }}</div>
              <small class="text-secondary">
                成人票{{ scope.row.attraction?.tickets?.adult || 0 }}张
                儿童票{{ scope.row.attraction?.tickets?.child || 0 }}张
              </small>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="checkInDate" label="日期" width="100">
          <template #default="scope">
            {{ scope.row.type === 'hotel' ? '入住' : '游览' }}：{{ scope.row.checkInDate }}
          </template>
        </el-table-column>
        <el-table-column label="总价" width="100">
          <template #default="scope">
            <span class="price-cell">¥{{ scope.row.totalPrice?.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.status === '待确认'"
              size="small"
              type="success"
              @click="confirmReservation(scope.row)"
            >
              确认
            </el-button>
            <el-button
              v-if="scope.row.status === '已确认' && scope.row.type === 'hotel'"
              size="small"
              type="primary"
              @click="checkIn(scope.row)"
            >
              入住
            </el-button>
            <el-button
              v-if="scope.row.status === '已入住' && scope.row.type === 'hotel'"
              size="small"
              type="warning"
              @click="checkOut(scope.row)"
            >
              退房
            </el-button>
            <el-button
              v-if="!['已退房', '已取消', '已完成'].includes(scope.row.status)"
              size="small"
              type="danger"
              @click="cancelReservation(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 新增预订对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      title="新增预订" 
      width="700px"
      :before-close="handleDialogClose"
    >
      <el-form 
        ref="addFormRef" 
        :model="addForm" 
        :rules="formRules" 
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select 
                v-model="addForm.customerId" 
                placeholder="请选择客户" 
                filterable
                remote
                :remote-method="searchCustomers"
                :loading="customerLoading"
              >
                <el-option 
                  v-for="customer in customerOptions" 
                  :key="customer.id" 
                  :label="`${customer.name} - ${customer.phone}`" 
                  :value="customer.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间" prop="roomId">
              <el-select 
                v-model="addForm.roomId" 
                placeholder="请选择房间" 
                filterable
              >
                <el-option 
                  v-for="room in availableRooms" 
                  :key="room.id" 
                  :label="`${room.roomNumber} - ${room.roomType?.typeName} - ￥${room.price}/夜`" 
                  :value="room.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkInDate">
              <el-date-picker
                v-model="addForm.checkInDate"
                type="date"
                placeholder="请选择入住日期"
                style="width: 100%"
                :disabled-date="disabledDate"
                @change="onDateChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退房日期" prop="checkOutDate">
              <el-date-picker
                v-model="addForm.checkOutDate"
                type="date"
                placeholder="请选择退房日期"
                style="width: 100%"
                :disabled-date="disabledCheckOutDate"
                @change="calculateTotalPrice"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住人数" prop="guestCount">
              <el-input-number 
                v-model="addForm.guestCount" 
                :min="1" 
                :max="10" 
                placeholder="请输入入住人数" 
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总价格">
              <el-input 
                v-model="totalPriceDisplay" 
                readonly 
                placeholder="自动计算" 
              >
                <template #prepend>￥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注">
          <el-input 
            v-model="addForm.notes" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息" 
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAddReservation" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 预订详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="预订详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentReservation.id">
        <el-descriptions-item label="订单号">{{ currentReservation.orderId }}</el-descriptions-item>
        <el-descriptions-item label="预订类型">
          <el-tag :type="currentReservation.type === 'hotel' ? 'primary' : 'warning'">
            {{ currentReservation.type === 'hotel' ? '酒店预订' : '景点门票' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReservation.status)">
            {{ currentReservation.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentReservation.customer?.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentReservation.customer?.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentReservation.customer?.email || '未填写' }}</el-descriptions-item>

        <!-- 酒店预订特有字段 -->
        <template v-if="currentReservation.type === 'hotel'">
          <el-descriptions-item label="房型">{{ currentReservation.room?.roomType?.typeName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentReservation.room?.roomNumber || '待分配' }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ currentReservation.checkInDate }}</el-descriptions-item>
          <el-descriptions-item label="退房日期">{{ currentReservation.checkOutDate }}</el-descriptions-item>
          <el-descriptions-item label="入住人数">{{ currentReservation.guestCount }}人</el-descriptions-item>
        </template>

        <!-- 景点预订特有字段 -->
        <template v-else>
          <el-descriptions-item label="景点名称">{{ currentReservation.attraction?.name }}</el-descriptions-item>
          <el-descriptions-item label="游览日期">{{ currentReservation.checkInDate }}</el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ currentReservation.attraction?.timeSlot === 'morning' ? '上午场' :
               currentReservation.attraction?.timeSlot === 'afternoon' ? '下午场' : '全天' }}
          </el-descriptions-item>
          <el-descriptions-item label="门票数量">
            成人{{ currentReservation.attraction?.tickets?.adult || 0 }}张，
            儿童{{ currentReservation.attraction?.tickets?.child || 0 }}张，
            学生{{ currentReservation.attraction?.tickets?.student || 0 }}张
          </el-descriptions-item>
        </template>

        <el-descriptions-item label="支付方式">
          {{ currentReservation.paymentMethod === 'WECHAT' ? '微信支付' :
             currentReservation.paymentMethod === 'ALIPAY' ? '支付宝' :
             currentReservation.paymentMethod === 'BANK_CARD' ? '银行卡' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="总价格">￥{{ currentReservation.totalPrice?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="交易号" v-if="currentReservation.transactionId">{{ currentReservation.transactionId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentReservation.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentReservation.notes || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import bookingManager from '@/utils/bookingManager'

const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const customerLoading = ref(false)
const reservations = ref([])
const currentReservation = ref({})
const customerOptions = ref([])
const availableRooms = ref([])
const addFormRef = ref()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  orderId: '',
  customerName: '',
  type: '',
  status: ''
})

const addForm = reactive({
  customerId: '',
  roomId: '',
  checkInDate: '',
  checkOutDate: '',
  guestCount: 1,
  notes: ''
})

const formRules = {
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  roomId: [
    { required: true, message: '请选择房间', trigger: 'change' }
  ],
  checkInDate: [
    { required: true, message: '请选择入住日期', trigger: 'change' }
  ],
  checkOutDate: [
    { required: true, message: '请选择退房日期', trigger: 'change' }
  ],
  guestCount: [
    { required: true, message: '请输入入住人数', trigger: 'blur' }
  ]
}

// 计算总价格
const totalPriceDisplay = computed(() => {
  if (!addForm.roomId || !addForm.checkInDate || !addForm.checkOutDate) {
    return '0.00'
  }
  
  const room = availableRooms.value.find(r => r.id === addForm.roomId)
  if (!room) return '0.00'
  
  const checkIn = new Date(addForm.checkInDate)
  const checkOut = new Date(addForm.checkOutDate)
  const days = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24))
  
  const total = room.price * days
  return total.toFixed(2)
})

// 获取预订列表
const fetchReservations = async () => {
  try {
    loading.value = true

    // 使用统一的预订管理器获取数据
    const allBookings = bookingManager.searchBookings({
      orderId: searchForm.orderId,
      customerName: searchForm.customerName,
      type: searchForm.type,
      status: searchForm.status
    })

    // 分页处理
    const startIndex = (pagination.value.current - 1) * pagination.value.size
    const endIndex = startIndex + pagination.value.size
    reservations.value = allBookings.slice(startIndex, endIndex)
    pagination.value.total = allBookings.length

  } catch (error) {
    console.error('获取预订列表失败:', error)
    ElMessage.error('获取预订列表失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusTypeMap = {
    '待确认': 'warning',
    '已确认': 'success',
    '已入住': 'primary',
    '已退房': 'info',
    '已完成': 'info',
    '已取消': 'danger'
  }
  return statusTypeMap[status] || 'info'
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchReservations()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.value.current = 1
  fetchReservations()
}

const viewDetail = (reservation) => {
  currentReservation.value = reservation
  showDetailDialog.value = true
}

// 导出数据
const exportData = () => {
  const filteredData = bookingManager.searchBookings({
    orderId: searchForm.orderId,
    customerName: searchForm.customerName,
    type: searchForm.type,
    status: searchForm.status
  })
  bookingManager.exportBookings(filteredData)
  ElMessage.success('数据导出成功')
}

// 搜索客户
const searchCustomers = async (query) => {
  if (!query) {
    customerOptions.value = []
    return
  }

  try {
    customerLoading.value = true
    // 使用mockUsers数据模拟客户搜索
    const { mockUsers } = await import('@/data/mockData')
    const filteredCustomers = mockUsers.filter(user =>
      user.role === 'user' &&
      (user.name.includes(query) || user.phone.includes(query))
    )
    customerOptions.value = filteredCustomers
  } catch (error) {
    console.error('搜索客户失败:', error)
  } finally {
    customerLoading.value = false
  }
}

// 获取可用房间
const fetchAvailableRooms = async () => {
  try {
    // 使用mockRoomTypes数据模拟可用房间
    const { mockRoomTypes } = await import('@/data/mockData')
    availableRooms.value = mockRoomTypes.map(roomType => ({
      id: roomType.id,
      roomNumber: `${roomType.id}01`, // 模拟房间号
      roomType: {
        id: roomType.id,
        typeName: roomType.name
      },
      price: roomType.price,
      status: 'available'
    }))
  } catch (error) {
    console.error('获取可用房间失败:', error)
    ElMessage.error('获取可用房间失败')
  }
}

// 日期禁用逐则
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

const disabledCheckOutDate = (time) => {
  if (!addForm.checkInDate) return false
  const checkInTime = new Date(addForm.checkInDate).getTime()
  return time.getTime() <= checkInTime // 退房日期必须在入住日期之后
}

// 日期变化时重新计算价格
const onDateChange = () => {
  if (addForm.checkInDate && addForm.checkOutDate) {
    calculateTotalPrice()
  }
}

const calculateTotalPrice = () => {
  // 由于使用了 computed，价格会自动计算
}

// 添加预订
const handleAddReservation = async () => {
  try {
    await addFormRef.value.validate()
    submitLoading.value = true
    
    // 格式化日期为 YYYY-MM-DD 格式
    const formatDate = (date) => {
      if (!date) return ''
      const d = new Date(date)
      return d.getFullYear() + '-' + 
             String(d.getMonth() + 1).padStart(2, '0') + '-' + 
             String(d.getDate()).padStart(2, '0')
    }
    
    const reservationData = {
      customerId: addForm.customerId,
      roomId: addForm.roomId,
      checkInDate: formatDate(addForm.checkInDate),
      checkOutDate: formatDate(addForm.checkOutDate),
      guestCount: addForm.guestCount,
      totalPrice: parseFloat(totalPriceDisplay.value),
      notes: addForm.notes || ''
    }
    
    await createReservation(reservationData)
    ElMessage.success('新增预订成功')
    showAddDialog.value = false
    resetAddForm()
    fetchReservations()
  } catch (error) {
    if (error !== false) {
      console.error('新增预订失败:', error)
      ElMessage.error('新增预订失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置添加表单
const resetAddForm = () => {
  addForm.customerId = ''
  addForm.roomId = ''
  addForm.checkInDate = ''
  addForm.checkOutDate = ''
  addForm.guestCount = 1
  addForm.notes = ''
  customerOptions.value = []
  
  nextTick(() => {
    addFormRef.value?.clearValidate()
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  showAddDialog.value = false
  resetAddForm()
}

const confirmReservation = async (reservation) => {
  try {
    const success = bookingManager.updateBookingStatus(reservation.id, '已确认')
    if (success) {
      ElMessage.success('确认预订成功')
      fetchReservations()
    } else {
      ElMessage.error('确认预订失败')
    }
  } catch (error) {
    console.error('确认预订失败:', error)
    ElMessage.error('确认预订失败')
  }
}

const checkIn = async (reservation) => {
  try {
    const success = bookingManager.updateBookingStatus(reservation.id, '已入住')
    if (success) {
      ElMessage.success('办理入住成功')
      fetchReservations()
    } else {
      ElMessage.error('办理入住失败')
    }
  } catch (error) {
    console.error('办理入住失败:', error)
    ElMessage.error('办理入住失败')
  }
}

const checkOut = async (reservation) => {
  try {
    const success = bookingManager.updateBookingStatus(reservation.id, '已退房')
    if (success) {
      ElMessage.success('办理退房成功')
      fetchReservations()
    } else {
      ElMessage.error('办理退房失败')
    }
  } catch (error) {
    console.error('办理退房失败:', error)
    ElMessage.error('办理退房失败')
  }
}

const cancelReservation = async (reservation) => {
  try {
    await ElMessageBox.confirm(`确定要取消预订 ${reservation.orderId} 吗？`, '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const success = bookingManager.updateBookingStatus(reservation.id, '已取消')
    if (success) {
      ElMessage.success('取消预订成功')
      fetchReservations()
    } else {
      ElMessage.error('取消预订失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预订失败:', error)
      ElMessage.error('取消预订失败')
    }
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  fetchReservations()
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchReservations()
}

onMounted(() => {
  Promise.all([
    fetchReservations(),
    fetchAvailableRooms()
  ])
})
</script>

<style scoped>
.reservations {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
  flex-shrink: 0;
}

.page-header h2 {
  color: var(--color-text-primary);
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.02em;
}

.search-card {
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.search-card :deep(.el-card__body) {
  padding: 24px;
}

.pagination {
  margin-top: 16px;
  text-align: center;
  padding: 16px 0;
  animation: fadeIn 0.3s ease-out;
  animation-delay: 0.2s;
  animation-fill-mode: both;
  flex-shrink: 0;
}

/* 主表格卡片特殊处理 */
.reservations > .el-card:last-of-type {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.reservations > .el-card:last-of-type :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.reservations :deep(.el-table) {
  flex: 1;
  min-height: calc(100vh - 420px);
}

.reservations :deep(.el-table .el-table__body-wrapper) {
  max-height: calc(100vh - 480px);
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

.el-descriptions {
  margin: 20px 0;
}

/* 状态按钮组优化 - Apple Design */
:deep(.el-table .el-button-group) {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

:deep(.el-button--success) {
  background: var(--color-success);
  border-color: var(--color-success);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
}

:deep(.el-button--success:hover) {
  background: var(--color-success);
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

:deep(.el-button--warning) {
  background: var(--color-warning);
  border-color: var(--color-warning);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
}

:deep(.el-button--warning:hover) {
  background: var(--color-warning);
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 预订状态标签优化 - Apple Design */
:deep(.el-tag) {
  font-weight: 500;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-xl);
  border: none;
  font-size: 0.75rem;
}

:deep(.el-tag--warning) {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

:deep(.el-tag--success) {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

:deep(.el-tag--primary) {
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-primary);
}

:deep(.el-tag--info) {
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
}

:deep(.el-tag--danger) {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

/* 表格行优化 - Apple Design */
:deep(.el-table) {
  background: var(--color-bg-primary);
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

/* 价格显示优化 - Apple Design */
.el-table :deep(.price-cell) {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 0.9375rem;
  letter-spacing: -0.01em;
}

/* 表单样式优化 - Apple Design */
:deep(.el-form--inline .el-form-item) {
  margin-right: 1.5rem;
  margin-bottom: 1rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

/* 日期选择器优化 */
:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: var(--radius-md);
}

/* 数字输入框优化 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: var(--radius-md);
}

/* 简化动画 - Apple Design */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.reservations {
  animation: fadeIn 0.3s ease-out;
}

/* 预订详情特殊样式 - Apple Design */
:deep(.el-descriptions__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
  background: var(--color-bg-secondary);
}

:deep(.el-descriptions__content) {
  color: var(--color-text-primary);
  font-weight: 400;
}

/* 按钮样式 - Apple Design */
:deep(.el-button--small) {
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  margin-right: 0.5rem;
  padding: 0.375rem 0.875rem;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px);
}

:deep(.el-button--danger) {
  background: var(--color-danger);
  border-color: var(--color-danger);
  color: white;
}

:deep(.el-button--danger:hover) {
  background: var(--color-danger);
  opacity: 0.9;
  box-shadow: var(--shadow-md);
}

:deep(.el-button--primary) {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

:deep(.el-button--primary:hover) {
  background: var(--color-primary-hover);
  border-color: var(--color-primary-hover);
  box-shadow: var(--shadow-md);
}

/* 卡片样式 */
:deep(.el-card) {
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

:deep(.el-card__body) {
  padding: 20px;
}

/* 辅助文本样式 */
.text-secondary {
  color: var(--color-text-secondary);
  font-size: 0.75rem;
}
</style>