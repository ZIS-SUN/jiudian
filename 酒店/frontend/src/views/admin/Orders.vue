<template>
  <div class="orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="header-actions">
        <el-button type="success" @click="handleExportOrders">
          <el-icon><Download /></el-icon>
          导出订单
        </el-button>
        <el-button type="primary" @click="showBatchDialog = true">
          <el-icon><Operation /></el-icon>
          批量操作
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon pending">📝</div>
        <div class="stat-content">
          <h3>待处理订单</h3>
          <div class="stat-value">{{ orderStats.pending }}</div>
          <p class="stat-desc">需要确认的订单</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon confirmed">✅</div>
        <div class="stat-content">
          <h3>已确认订单</h3>
          <div class="stat-value">{{ orderStats.confirmed }}</div>
          <p class="stat-desc">等待入住的订单</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon revenue">💰</div>
        <div class="stat-content">
          <h3>今日收入</h3>
          <div class="stat-value">¥{{ orderStats.todayRevenue.toLocaleString() }}</div>
          <p class="stat-desc">今日订单总收入</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon total">📊</div>
        <div class="stat-content">
          <h3>本月订单</h3>
          <div class="stat-value">{{ orderStats.monthlyOrders }}</div>
          <p class="stat-desc">本月订单总数</p>
        </div>
      </div>
    </div>

    <!-- 搜索和过滤 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderId"
            placeholder="请输入订单号"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="客户信息">
          <el-input
            v-model="searchForm.customerInfo"
            placeholder="姓名/手机号/邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="订单类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="酒店订单" value="hotel" />
            <el-option label="景点门票" value="attraction" />
          </el-select>
        </el-form-item>

        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已入住" value="已入住" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>

        <el-form-item label="支付状态">
          <el-select v-model="searchForm.paymentStatus" placeholder="全部" clearable style="width: 100px">
            <el-option label="全部" value="" />
            <el-option label="已支付" value="paid" />
            <el-option label="未支付" value="unpaid" />
            <el-option label="退款中" value="refunding" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </el-form-item>

        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card>
      <div class="table-header">
        <el-checkbox
          v-model="selectAll"
          :indeterminate="isIndeterminate"
          @change="handleSelectAll"
        >
          全选
        </el-checkbox>
        <div class="selected-info" v-if="selectedOrders.length > 0">
          已选择 {{ selectedOrders.length }} 项
          <el-button type="danger" size="small" @click="batchCancel">批量取消</el-button>
          <el-button type="success" size="small" @click="batchConfirm">批量确认</el-button>
        </div>
      </div>

      <el-table
        :data="orders"
        :loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column label="订单信息" min-width="200">
          <template #default="scope">
            <div class="order-info">
              <div class="order-id">{{ scope.row.orderId }}</div>
              <div class="order-meta">
                <el-tag :type="scope.row.type === 'hotel' ? 'primary' : 'warning'" size="small">
                  {{ scope.row.type === 'hotel' ? '酒店' : '景点' }}
                </el-tag>
                <span class="order-time">{{ formatDateTime(scope.row.createdTime) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="客户信息" min-width="150">
          <template #default="scope">
            <div class="customer-info">
              <div class="customer-name">{{ scope.row.customer?.name }}</div>
              <div class="customer-contact">{{ scope.row.customer?.phone }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="订单内容" min-width="200">
          <template #default="scope">
            <div class="order-content">
              <div v-if="scope.row.type === 'hotel'" class="hotel-order">
                <div class="content-name">{{ scope.row.room?.roomType?.typeName }}</div>
                <div class="content-details">
                  <span>{{ scope.row.checkInDate }}</span> 至 <span>{{ scope.row.checkOutDate }}</span>
                </div>
                <div class="content-meta">
                  {{ scope.row.guestCount }}人 · {{ calculateNights(scope.row.checkInDate, scope.row.checkOutDate) }}晚
                </div>
              </div>
              <div v-else class="attraction-order">
                <div class="content-name">{{ scope.row.attraction?.name }}</div>
                <div class="content-details">游览日期：{{ scope.row.checkInDate }}</div>
                <div class="content-meta">
                  成人{{ scope.row.attraction?.tickets?.adult || 0 }}张
                  儿童{{ scope.row.attraction?.tickets?.child || 0 }}张
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="金额" width="120">
          <template #default="scope">
            <div class="amount-info">
              <div class="total-amount">¥{{ scope.row.totalPrice?.toLocaleString() }}</div>
              <el-tag
                :type="getPaymentStatusType(scope.row.paymentStatus)"
                size="small"
              >
                {{ getPaymentStatusText(scope.row.paymentStatus) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" @click="viewOrderDetail(scope.row)">详情</el-button>

              <el-button
                v-if="scope.row.status === '待确认'"
                size="small"
                type="success"
                @click="confirmOrder(scope.row)"
              >
                确认
              </el-button>

              <el-button
                v-if="scope.row.status === '已确认' && scope.row.type === 'hotel'"
                size="small"
                type="primary"
                @click="checkInOrder(scope.row)"
              >
                入住
              </el-button>

              <el-button
                v-if="scope.row.status === '已入住' && scope.row.type === 'hotel'"
                size="small"
                type="warning"
                @click="checkOutOrder(scope.row)"
              >
                退房
              </el-button>

              <el-dropdown v-if="hasMoreActions(scope.row)" trigger="click" placement="bottom-end">
                <el-button size="small" type="info">
                  更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="canRefund(scope.row)"
                      @click="refundOrder(scope.row)"
                    >
                      <el-icon><RefreshLeft /></el-icon>退款
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="canCancel(scope.row)"
                      @click="cancelOrder(scope.row)"
                    >
                      <el-icon><Close /></el-icon>取消订单
                    </el-dropdown-item>
                    <el-dropdown-item @click="printOrder(scope.row)">
                      <el-icon><Printer /></el-icon>打印订单
                    </el-dropdown-item>
                    <el-dropdown-item @click="sendOrderEmail(scope.row)">
                      <el-icon><Message /></el-icon>发送邮件
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
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

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder.id" class="order-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="订单号">{{ currentOrder.orderId }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">
              <el-tag :type="currentOrder.type === 'hotel' ? 'primary' : 'warning'">
                {{ currentOrder.type === 'hotel' ? '酒店订单' : '景点门票' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)">
                {{ currentOrder.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentOrder.createdTime }}</el-descriptions-item>
            <el-descriptions-item label="支付状态">
              <el-tag :type="getPaymentStatusType(currentOrder.paymentStatus)">
                {{ getPaymentStatusText(currentOrder.paymentStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ getPaymentMethodText(currentOrder.paymentMethod) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 客户信息 -->
        <div class="detail-section">
          <h3>客户信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="客户姓名">{{ currentOrder.customer?.name }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentOrder.customer?.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱地址">{{ currentOrder.customer?.email || '未提供' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 订单内容 -->
        <div class="detail-section">
          <h3>订单内容</h3>
          <template v-if="currentOrder.type === 'hotel'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="房型">{{ currentOrder.room?.roomType?.typeName }}</el-descriptions-item>
              <el-descriptions-item label="房间号">{{ currentOrder.room?.roomNumber || '待分配' }}</el-descriptions-item>
              <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
              <el-descriptions-item label="退房日期">{{ currentOrder.checkOutDate }}</el-descriptions-item>
              <el-descriptions-item label="入住人数">{{ currentOrder.guestCount }}人</el-descriptions-item>
              <el-descriptions-item label="住宿天数">{{ calculateNights(currentOrder.checkInDate, currentOrder.checkOutDate) }}晚</el-descriptions-item>
            </el-descriptions>
          </template>
          <template v-else>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="景点名称">{{ currentOrder.attraction?.name }}</el-descriptions-item>
              <el-descriptions-item label="游览日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
              <el-descriptions-item label="时间段">
                {{ formatTimeSlot(currentOrder.attraction?.timeSlot) }}
              </el-descriptions-item>
              <el-descriptions-item label="门票数量">
                成人{{ currentOrder.attraction?.tickets?.adult || 0 }}张，
                儿童{{ currentOrder.attraction?.tickets?.child || 0 }}张，
                学生{{ currentOrder.attraction?.tickets?.student || 0 }}张
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </div>

        <!-- 费用信息 -->
        <div class="detail-section">
          <h3>费用信息</h3>
          <div class="price-breakdown">
            <div class="price-row">
              <span>订单金额</span>
              <span class="amount">¥{{ currentOrder.totalPrice?.toLocaleString() }}</span>
            </div>
            <div class="price-row total">
              <span>实付金额</span>
              <span class="amount">¥{{ currentOrder.totalPrice?.toLocaleString() }}</span>
            </div>
            <div v-if="currentOrder.transactionId" class="price-row">
              <span>交易号</span>
              <span>{{ currentOrder.transactionId }}</span>
            </div>
          </div>
        </div>

        <!-- 备注信息 -->
        <div class="detail-section" v-if="currentOrder.notes">
          <h3>备注信息</h3>
          <el-input
            v-model="currentOrder.notes"
            type="textarea"
            :rows="3"
            readonly
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="printOrder(currentOrder)">打印订单</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量操作弹窗 -->
    <el-dialog v-model="showBatchDialog" title="批量操作" width="500px">
      <div class="batch-content">
        <el-alert
          title="批量操作说明"
          description="请选择要执行的批量操作类型，操作将应用于当前已选中的订单"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        />

        <el-form :model="batchForm" label-width="100px">
          <el-form-item label="操作类型">
            <el-select v-model="batchForm.action" placeholder="请选择操作类型">
              <el-option label="批量确认" value="confirm" />
              <el-option label="批量取消" value="cancel" />
              <el-option label="批量导出" value="export" />
              <el-option label="批量发送邮件" value="email" />
            </el-select>
          </el-form-item>

          <el-form-item v-if="batchForm.action === 'cancel'" label="取消原因">
            <el-input
              v-model="batchForm.reason"
              type="textarea"
              :rows="3"
              placeholder="请输入取消原因"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button type="primary" @click="executeBatchOperation" :loading="batchProcessing">
          执行操作
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download, Operation, Search, RefreshLeft, Close, Printer, Message, ArrowDown
} from '@element-plus/icons-vue'
import { 
  getOrders, 
  getOrderStatistics, 
  getOrderById,
  batchUpdateOrderStatus,
  processRefund,
  exportOrders
} from '@/api/orders'

// 响应式数据
const loading = ref(false)
const orders = ref([])
const selectedOrders = ref([])
const selectAll = ref(false)
const showDetailDialog = ref(false)
const showBatchDialog = ref(false)
const batchProcessing = ref(false)
const currentOrder = ref({})

// 搜索表单
const searchForm = reactive({
  orderId: '',
  customerInfo: '',
  type: '',
  status: '',
  paymentStatus: '',
  dateRange: []
})

// 批量操作表单
const batchForm = reactive({
  action: '',
  reason: ''
})

// 分页
const pagination = ref({
  current: 1,
  size: 20,
  total: 0
})

// 计算属性
const isIndeterminate = computed(() => {
  const selectedCount = selectedOrders.value.length
  const totalCount = orders.value.length
  return selectedCount > 0 && selectedCount < totalCount
})

// 订单统计 - 使用响应式数据
const orderStats = ref({
  pending: 0,
  confirmed: 0,
  todayRevenue: 0,
  monthlyOrders: 0
})

// 方法
const fetchOrders = async () => {
  loading.value = true

  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      orderId: searchForm.orderId || undefined,
      customerInfo: searchForm.customerInfo || undefined,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined,
      paymentStatus: searchForm.paymentStatus || undefined,
      dateRange: searchForm.dateRange?.length === 2 ? searchForm.dateRange.join(',') : undefined
    }

    const response = await getOrders(params)
    if (response.code === 200) {
      orders.value = response.data.records || []
      pagination.value.total = response.data.total || 0
      pagination.value.current = response.data.current || 1
    } else {
      ElMessage.error(response.message || '获取订单数据失败')
    }
  } catch (error) {
    console.error('获取订单数据失败:', error)
    ElMessage.error('获取订单数据失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchOrderStats = async () => {
  try {
    const response = await getOrderStatistics()
    if (response.code === 200) {
      orderStats.value = {
        pending: response.data.pending || 0,
        confirmed: response.data.confirmed || 0,
        todayRevenue: response.data.todayRevenue || 0,
        monthlyOrders: response.data.monthlyOrders || 0
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchOrders()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    if (key === 'dateRange') {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  pagination.value.current = 1
  fetchOrders()
}

const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

const handleSelectAll = (val) => {
  // 由 element-plus 自动处理
}

const viewOrderDetail = (order) => {
  currentOrder.value = order
  showDetailDialog.value = true
}

const confirmOrder = async (order) => {
  try {
    const response = await batchUpdateOrderStatus({
      orderIds: [order.id],
      status: 'CONFIRMED'
    })
    if (response.code === 200) {
      ElMessage.success('订单确认成功')
      fetchOrders()
    } else {
      ElMessage.error(response.message || '订单确认失败')
    }
  } catch (error) {
    console.error('订单确认失败:', error)
    ElMessage.error('订单确认失败')
  }
}

const checkInOrder = async (order) => {
  try {
    const response = await batchUpdateOrderStatus({
      orderIds: [order.id],
      status: 'CHECKED_IN'
    })
    if (response.code === 200) {
      ElMessage.success('办理入住成功')
      fetchOrders()
    } else {
      ElMessage.error(response.message || '办理入住失败')
    }
  } catch (error) {
    console.error('办理入住失败:', error)
    ElMessage.error('办理入住失败')
  }
}

const checkOutOrder = async (order) => {
  try {
    const response = await batchUpdateOrderStatus({
      orderIds: [order.id],
      status: 'CHECKED_OUT'
    })
    if (response.code === 200) {
      ElMessage.success('办理退房成功')
      fetchOrders()
    } else {
      ElMessage.error(response.message || '办理退房失败')
    }
  } catch (error) {
    console.error('办理退房失败:', error)
    ElMessage.error('办理退房失败')
  }
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(`确定要取消订单 ${order.orderId} 吗？`, '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await batchUpdateOrderStatus({
      orderIds: [order.id],
      status: 'CANCELLED'
    })
    if (response.code === 200) {
      ElMessage.success('取消订单成功')
      fetchOrders()
    } else {
      ElMessage.error(response.message || '取消订单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const refundOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要为订单 ${order.orderId} 申请退款吗？退款金额：¥${order.totalPrice}`,
      '确认退款',
      {
        confirmButtonText: '确定退款',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 这里应该调用退款接口
    ElMessage.success('退款申请已提交，请等待处理')
    // TODO: 更新订单状态为退款中
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退款申请失败')
    }
  }
}

const printOrder = (order) => {
  // TODO: 实现打印功能
  ElMessage.info('打印功能开发中')
}

const sendOrderEmail = (order) => {
  // TODO: 实现邮件发送功能
  ElMessage.info('邮件发送功能开发中')
}


const batchConfirm = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择要操作的订单')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要批量确认选中的 ${selectedOrders.value.length} 个订单吗？`,
      '批量确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 使用新的批量更新API
    await batchUpdateStatus('CONFIRMED')

    ElMessage.success(`批量确认成功，共处理 ${selectedOrders.value.length} 个订单`)
    fetchOrders()
    selectedOrders.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量确认失败')
    }
  }
}

const batchCancel = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择要操作的订单')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要批量取消选中的 ${selectedOrders.value.length} 个订单吗？`,
      '批量取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 使用新的批量更新API
    await batchUpdateStatus('CANCELLED')

    ElMessage.success(`批量取消成功，共处理 ${selectedOrders.value.length} 个订单`)
    fetchOrders()
    selectedOrders.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

const executeBatchOperation = async () => {
  if (!batchForm.action) {
    ElMessage.warning('请选择操作类型')
    return
  }

  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择要操作的订单')
    return
  }

  batchProcessing.value = true

  try {
    const orderIds = selectedOrders.value.map(order => order.id)
    
    switch (batchForm.action) {
      case 'confirm':
        await batchUpdateStatus('CONFIRMED')
        break
      case 'cancel':
        await batchUpdateStatus('CANCELLED')
        break
      case 'export':
        await handleExportOrders()
        break
      case 'email':
        // TODO: 实现批量邮件发送
        ElMessage.info('批量邮件发送功能开发中')
        break
    }

    showBatchDialog.value = false
    batchForm.action = ''
    batchForm.reason = ''
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败')
  } finally {
    batchProcessing.value = false
  }
}

// 批量更新状态
const batchUpdateStatus = async (status) => {
  try {
    await ElMessageBox.confirm(
      `确定要批量${status === 'CONFIRMED' ? '确认' : '取消'}选中的 ${selectedOrders.value.length} 个订单吗？`,
      `批量${status === 'CONFIRMED' ? '确认' : '取消'}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const orderIds = selectedOrders.value.map(order => order.id)
    const response = await batchUpdateOrderStatus({
      orderIds,
      status
    })

    if (response.code === 200) {
      ElMessage.success(`批量${status === 'CONFIRMED' ? '确认' : '取消'}成功`)
      fetchOrders() // 刷新订单列表
      selectedOrders.value = []
    } else {
      ElMessage.error(response.message || '批量操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量操作失败:', error)
      ElMessage.error('批量操作失败')
    }
  }
}

// 导出订单
const handleExportOrders = async () => {
  try {
    const params = {
      orderId: searchForm.orderId || undefined,
      customerInfo: searchForm.customerInfo || undefined,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined,
      paymentStatus: searchForm.paymentStatus || undefined,
      dateRange: searchForm.dateRange?.length === 2 ? searchForm.dateRange.join(',') : undefined
    }

    const response = await exportOrders(params)
    
    // 创建下载链接
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.style.display = 'none'
    link.href = url
    link.download = `订单数据_${new Date().toISOString().slice(0, 10)}.xlsx`
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('订单导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('订单导出失败')
  }
}

// 辅助方法
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

const getPaymentStatusType = (status) => {
  const statusTypeMap = {
    'paid': 'success',
    'unpaid': 'warning',
    'refunding': 'warning',
    'refunded': 'info'
  }
  return statusTypeMap[status] || 'info'
}

const getPaymentStatusText = (status) => {
  const statusTextMap = {
    'paid': '已支付',
    'unpaid': '未支付',
    'refunding': '退款中',
    'refunded': '已退款'
  }
  return statusTextMap[status] || '未知'
}

const getPaymentMethodText = (method) => {
  const methodTextMap = {
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝',
    'BANK_CARD': '银行卡',
    'BALANCE': '余额支付'
  }
  return methodTextMap[method] || '未知'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatTimeSlot = (timeSlot) => {
  const timeSlotMap = {
    'morning': '上午场 (09:00-12:00)',
    'afternoon': '下午场 (13:00-17:00)',
    'fullday': '全天 (09:00-17:00)'
  }
  return timeSlotMap[timeSlot] || '未知'
}

const calculateNights = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return 0
  const checkInDate = new Date(checkIn)
  const checkOutDate = new Date(checkOut)
  const diffTime = checkOutDate.getTime() - checkInDate.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 ? diffDays : 0
}

const hasMoreActions = (order) => {
  return canRefund(order) || canCancel(order) || true // 总是显示打印和邮件选项
}

const canRefund = (order) => {
  return order.paymentStatus === 'paid' && !['已取消', '已退款'].includes(order.status)
}

const canCancel = (order) => {
  return !['已取消', '已退房', '已完成'].includes(order.status)
}

const handlePageChange = (page) => {
  pagination.value.current = page
  fetchOrders()
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchOrders()
}

// 页面初始化
onMounted(() => {
  fetchOrders()
  fetchOrderStats()
})
</script>

<style scoped>
.orders {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding: 0 4px;
}

.page-header h2 {
  color: var(--color-text-primary);
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.02em;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: var(--color-bg-primary);
  padding: 1.5rem;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.stat-icon.confirmed {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
}

.stat-content h3 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.stat-desc {
  margin: 0;
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 1rem;
}

.search-card :deep(.el-card__body) {
  padding: 24px;
}

/* 表格头部 */
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding: 0 4px;
}

.selected-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

/* 表格内容样式 */
.order-info .order-id {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.order-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.order-time {
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
}

.customer-info .customer-name {
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.customer-contact {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.order-content .content-name {
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.content-details {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.content-meta {
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
}

.amount-info .total-amount {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 1rem;
  margin-bottom: 0.25rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

/* 分页 */
.pagination {
  margin-top: 1rem;
  text-align: center;
  padding: 16px 0;
}

/* 订单详情 */
.order-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 2rem;
}

.detail-section h3 {
  margin: 0 0 1rem 0;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  border-bottom: 2px solid var(--color-primary);
  padding-bottom: 0.5rem;
}

.price-breakdown {
  background: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: var(--radius-md);
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
}

.price-row:last-child {
  border-bottom: none;
}

.price-row.total {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 1.125rem;
  border-top: 2px solid var(--color-primary);
  margin-top: 0.5rem;
  padding-top: 0.75rem;
}

.amount {
  font-weight: 600;
}

/* 批量操作 */
.batch-content {
  padding: 1rem 0;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

/* Element Plus 样式覆盖 */
:deep(.el-card) {
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

:deep(.el-table) {
  background: var(--color-bg-primary);
  border-radius: var(--radius-md);
}

:deep(.el-table th) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-weight: 600;
  font-size: 0.875rem;
}

:deep(.el-table tbody tr:hover) {
  background: var(--color-bg-secondary);
}

:deep(.el-button--small) {
  border-radius: var(--radius-sm);
  font-weight: 500;
  padding: 0.375rem 0.875rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .action-buttons .el-button {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
  }

  .order-info .order-id {
    font-size: 0.875rem;
  }

  .content-name {
    font-size: 0.875rem;
  }
}
</style>