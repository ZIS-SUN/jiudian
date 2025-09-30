<template>
  <div class="profile-page">
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
          <router-link to="/profile" class="nav-link active">个人中心</router-link>
          <router-link to="/contact" class="nav-link">联系我们</router-link>

          <!-- 登录后显示用户信息和登出 -->
          <span class="user-info">{{ userInfo?.name || userInfo?.username }}</span>
          <button class="logout-btn" @click="handleLogout">退出登录</button>

          <!-- 管理员专用链接 -->
          <router-link v-if="isAdmin" to="/admin" class="nav-link admin-link">管理后台</router-link>
        </div>
      </div>
    </nav>

    <div class="page-content">
      <div class="container">
        <div class="profile-layout">
          <!-- 侧边栏 -->
          <div class="profile-sidebar">
            <div class="sidebar-menu">
              <div
                class="menu-item"
                :class="{ active: activeTab === 'overview' }"
                @click="activeTab = 'overview'"
              >
                <el-icon><User /></el-icon>
                <span>个人概览</span>
              </div>
              <div
                class="menu-item"
                :class="{ active: activeTab === 'balance' }"
                @click="activeTab = 'balance'"
              >
                <el-icon><Wallet /></el-icon>
                <span>余额管理</span>
              </div>
              <div
                class="menu-item"
                :class="{ active: activeTab === 'profile-edit' }"
                @click="activeTab = 'profile-edit'"
              >
                <el-icon><Edit /></el-icon>
                <span>编辑资料</span>
              </div>
              <div
                class="menu-item"
                :class="{ active: activeTab === 'security' }"
                @click="activeTab = 'security'"
              >
                <el-icon><Lock /></el-icon>
                <span>账户安全</span>
              </div>
            </div>
          </div>

          <!-- 主内容区域 -->
          <div class="profile-content">
            <!-- 个人概览 -->
            <div v-if="activeTab === 'overview'" class="tab-content">
              <h2>个人概览</h2>
              <div class="overview-cards">
                <div class="overview-card">
                  <div class="card-icon balance">💰</div>
                  <div class="card-content">
                    <h3>账户余额</h3>
                    <div class="card-value">¥{{ userBalance.toLocaleString() }}</div>
                    <p class="card-desc">可用于预订支付</p>
                  </div>
                </div>

                <div class="overview-card">
                  <div class="card-icon bookings">📅</div>
                  <div class="card-content">
                    <h3>预订记录</h3>
                    <div class="card-value">{{ totalBookings }}</div>
                    <p class="card-desc">历史预订总数</p>
                  </div>
                </div>

                <div class="overview-card">
                  <div class="card-icon spending">💳</div>
                  <div class="card-content">
                    <h3>累计消费</h3>
                    <div class="card-value">¥{{ totalSpending.toLocaleString() }}</div>
                    <p class="card-desc">总消费金额</p>
                  </div>
                </div>

                <div class="overview-card">
                  <div class="card-icon vip">⭐</div>
                  <div class="card-content">
                    <h3>会员等级</h3>
                    <div class="card-value">{{ vipLevel }}</div>
                    <p class="card-desc">享受专属优惠</p>
                  </div>
                </div>
              </div>

              <!-- 最近预订 -->
              <div class="recent-bookings">
                <h3>最近预订</h3>
                <div class="booking-list" v-if="recentBookings.length > 0">
                  <div
                    v-for="booking in recentBookings"
                    :key="booking.id"
                    class="booking-item"
                  >
                    <div class="booking-info">
                      <h4>{{ booking.roomName || booking.attractionName }}</h4>
                      <p class="booking-date">{{ booking.checkInDate }}</p>
                    </div>
                    <div class="booking-status">
                      <el-tag :type="getStatusType(booking.status)">
                        {{ booking.statusText || booking.status }}
                      </el-tag>
                    </div>
                    <div class="booking-amount">¥{{ booking.totalAmount.toLocaleString() }}</div>
                  </div>
                </div>
                <div v-else class="no-bookings">
                  <p>暂无预订记录</p>
                </div>
              </div>
            </div>

            <!-- 余额管理 -->
            <div v-if="activeTab === 'balance'" class="tab-content">
              <h2>余额管理</h2>

              <div class="balance-overview">
                <div class="balance-card">
                  <div class="balance-header">
                    <h3>当前余额</h3>
                    <div class="balance-amount">¥{{ userBalance.toLocaleString() }}</div>
                  </div>
                  <div class="balance-actions">
                    <el-button type="primary" @click="showRechargeDialog = true">
                      <el-icon><Plus /></el-icon>
                      充值
                    </el-button>
                    <el-button @click="showWithdrawDialog = true">
                      <el-icon><Minus /></el-icon>
                      提现
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 交易记录 -->
              <div class="transaction-history">
                <h3>交易记录</h3>
                <div class="transaction-list">
                  <div
                    v-for="transaction in transactions"
                    :key="transaction.id"
                    class="transaction-item"
                  >
                    <div class="transaction-icon">
                      <span v-if="transaction.type === 'recharge'">⬆️</span>
                      <span v-else-if="transaction.type === 'payment'">💳</span>
                      <span v-else>⬇️</span>
                    </div>
                    <div class="transaction-info">
                      <h4>{{ transaction.description }}</h4>
                      <p class="transaction-time">{{ transaction.time }}</p>
                    </div>
                    <div class="transaction-amount" :class="transaction.type">
                      {{ transaction.type === 'recharge' ? '+' : '-' }}¥{{ Math.abs(transaction.amount).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 编辑资料 -->
            <div v-if="activeTab === 'profile-edit'" class="tab-content">
              <h2>编辑资料</h2>
              <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="120px">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="用户名" prop="username">
                      <el-input v-model="profileForm.username" disabled />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="姓名" prop="name">
                      <el-input v-model="profileForm.name" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="手机号" prop="phone">
                      <el-input v-model="profileForm.phone" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="profileForm.email" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="生日" prop="birthday">
                  <el-date-picker
                    v-model="profileForm.birthday"
                    type="date"
                    placeholder="选择生日"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
                  <el-button @click="resetProfile">重置</el-button>
                </el-form-item>
              </el-form>
            </div>

            <!-- 账户安全 -->
            <div v-if="activeTab === 'security'" class="tab-content">
              <h2>账户安全</h2>
              <div class="security-items">
                <div class="security-item">
                  <div class="security-info">
                    <h3>登录密码</h3>
                    <p>定期更新密码，保护账户安全</p>
                  </div>
                  <el-button @click="showPasswordDialog = true">修改密码</el-button>
                </div>

                <div class="security-item">
                  <div class="security-info">
                    <h3>手机验证</h3>
                    <p>已绑定手机：{{ maskPhone(userInfo?.phone) }}</p>
                  </div>
                  <el-button>更换手机</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <el-dialog v-model="showRechargeDialog" title="账户充值" width="400px">
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="50000"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        <div class="quick-amounts">
          <el-button
            v-for="amount in [100, 200, 500, 1000, 2000]"
            :key="amount"
            size="small"
            @click="rechargeForm.amount = amount"
          >
            ¥{{ amount }}
          </el-button>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showRechargeDialog = false">取消</el-button>
        <el-button type="primary" @click="processRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>

    <!-- 提现弹窗 -->
    <el-dialog v-model="showWithdrawDialog" title="余额提现" width="400px">
      <el-form :model="withdrawForm" :rules="withdrawRules" ref="withdrawFormRef">
        <el-form-item label="提现金额" prop="amount">
          <el-input-number
            v-model="withdrawForm.amount"
            :min="1"
            :max="userBalance"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="提现方式" prop="method">
          <el-radio-group v-model="withdrawForm.method">
            <el-radio label="bank">银行卡</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="wechat">微信</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWithdrawDialog = false">取消</el-button>
        <el-button type="primary" @click="processWithdraw" :loading="withdrawing">确认提现</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Wallet, Edit, Lock, Plus, Minus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import bookingManager from '@/utils/bookingManager'
import balanceManager from '@/utils/balanceManager'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const activeTab = ref('overview')
const userBalance = ref(0)
const saving = ref(false)
const recharging = ref(false)
const withdrawing = ref(false)
const changingPassword = ref(false)

// 弹窗显示状态
const showRechargeDialog = ref(false)
const showWithdrawDialog = ref(false)
const showPasswordDialog = ref(false)

// 表单引用
const profileFormRef = ref()
const rechargeFormRef = ref()
const withdrawFormRef = ref()
const passwordFormRef = ref()

// 用户信息
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userInfo.value?.role === 'admin')

// 用户统计数据
const totalBookings = computed(() => {
  if (!userInfo.value?.id) return 0
  return bookingManager.getUserBookings(userInfo.value.id).length
})

const totalSpending = computed(() => {
  if (!userInfo.value?.id) return 0
  return bookingManager.getUserBookings(userInfo.value.id)
    .reduce((sum, booking) => sum + booking.totalPrice, 0)
})

const vipLevel = computed(() => {
  const spending = totalSpending.value
  if (spending >= 50000) return '钻石会员'
  if (spending >= 20000) return '金牌会员'
  if (spending >= 10000) return '银牌会员'
  if (spending >= 5000) return '铜牌会员'
  return '普通会员'
})

const recentBookings = computed(() => {
  if (!userInfo.value?.id) return []
  return bookingManager.getUserBookings(userInfo.value.id).slice(0, 5)
})

// 表单数据
const profileForm = reactive({
  username: '',
  name: '',
  phone: '',
  email: '',
  birthday: ''
})

const rechargeForm = reactive({
  amount: 100
})

const withdrawForm = reactive({
  amount: 0,
  method: 'bank'
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, max: 50000, message: '充值金额必须在1-50000之间', trigger: 'blur' }
  ]
}

const withdrawRules = {
  amount: [
    { required: true, message: '请输入提现金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '提现金额必须大于0', trigger: 'blur' }
  ],
  method: [
    { required: true, message: '请选择提现方式', trigger: 'change' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 交易记录
const transactions = ref([
  {
    id: 1,
    type: 'recharge',
    description: '账户充值',
    amount: 1000,
    time: '2024-12-01 14:30'
  },
  {
    id: 2,
    type: 'payment',
    description: '预订支付 - 豪华海景大床房',
    amount: -688,
    time: '2024-12-01 15:45'
  },
  {
    id: 3,
    type: 'recharge',
    description: '账户充值',
    amount: 500,
    time: '2024-11-28 10:20'
  }
])

// 方法
const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

const getStatusType = (status) => {
  const statusTypeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'CHECKED_IN': 'primary',
    'CHECKED_OUT': 'info',
    'CANCELLED': 'danger',
    '待确认': 'warning',
    '已确认': 'success',
    '已入住': 'primary',
    '已退房': 'info',
    '已取消': 'danger'
  }
  return statusTypeMap[status] || 'info'
}

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 初始化用户资料表单
const initProfileForm = () => {
  if (userInfo.value) {
    profileForm.username = userInfo.value.username || ''
    profileForm.name = userInfo.value.name || ''
    profileForm.phone = userInfo.value.phone || ''
    profileForm.email = userInfo.value.email || ''
    profileForm.birthday = userInfo.value.birthday || ''
  }
}

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    saving.value = true

    // 模拟保存
    setTimeout(() => {
      // 更新用户信息
      userStore.updateUserInfo({
        ...userInfo.value,
        name: profileForm.name,
        phone: profileForm.phone,
        email: profileForm.email,
        birthday: profileForm.birthday
      })

      saving.value = false
      ElMessage.success('资料保存成功')
    }, 1000)
  } catch (error) {
    console.log('表单验证失败')
  }
}

const resetProfile = () => {
  initProfileForm()
}

const processRecharge = async () => {
  try {
    await rechargeFormRef.value.validate()
    recharging.value = true

    // 模拟充值过程
    setTimeout(() => {
      const result = balanceManager.addBalance(rechargeForm.amount, '账户充值')

      if (result.success) {
        userBalance.value = result.currentBalance
        // 重新获取交易记录
        transactions.value = balanceManager.getTransactions()

        recharging.value = false
        showRechargeDialog.value = false
        rechargeForm.amount = 100
        ElMessage.success(`充值成功！余额：¥${userBalance.value.toLocaleString()}`)
      } else {
        recharging.value = false
        ElMessage.error('充值失败，请重试')
      }
    }, 2000)
  } catch (error) {
    console.log('表单验证失败')
  }
}

const processWithdraw = async () => {
  try {
    await withdrawFormRef.value.validate()

    if (withdrawForm.amount > userBalance.value) {
      ElMessage.error('提现金额不能超过账户余额')
      return
    }

    withdrawing.value = true

    // 模拟提现过程
    setTimeout(() => {
      userBalance.value -= withdrawForm.amount

      // 添加交易记录
      transactions.value.unshift({
        id: Date.now(),
        type: 'withdraw',
        description: `${withdrawForm.method === 'bank' ? '银行卡' : withdrawForm.method === 'alipay' ? '支付宝' : '微信'}提现`,
        amount: -withdrawForm.amount,
        time: new Date().toLocaleString()
      })

      // 保存到localStorage
      localStorage.setItem('userBalance', userBalance.value.toString())
      localStorage.setItem('userTransactions', JSON.stringify(transactions.value))

      withdrawing.value = false
      showWithdrawDialog.value = false
      withdrawForm.amount = 0
      withdrawForm.method = 'bank'
      ElMessage.success('提现申请已提交，1-3个工作日到账')
    }, 2000)
  } catch (error) {
    console.log('表单验证失败')
  }
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true

    // 模拟密码修改
    setTimeout(() => {
      changingPassword.value = false
      showPasswordDialog.value = false
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      ElMessage.success('密码修改成功')
    }, 1500)
  } catch (error) {
    console.log('表单验证失败')
  }
}

// 获取用户余额
const getUserBalance = () => {
  userBalance.value = balanceManager.getBalance()
}

// 获取交易记录
const getUserTransactions = () => {
  transactions.value = balanceManager.getTransactions()
}

// 页面初始化
onMounted(() => {
  userStore.initUserInfo()
  initProfileForm()
  getUserBalance()
  getUserTransactions()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

/* 导航栏样式 - 重用之前的样式 */
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

.user-info {
  color: var(--color-text-primary);
  font-weight: 500;
  margin-right: 1rem;
}

.logout-btn {
  background: var(--color-danger);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: 500;
  transition: all var(--transition-fast);
}

.logout-btn:hover {
  background: var(--color-danger);
  opacity: 0.9;
  transform: translateY(-1px);
}

.admin-link {
  background: var(--color-text-primary) !important;
  color: var(--color-bg-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  opacity: 1 !important;
}

.page-content {
  padding-top: 76px;
  padding-bottom: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 个人中心布局 */
.profile-layout {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
}

/* 侧边栏 */
.profile-sidebar {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  height: fit-content;
  position: sticky;
  top: 100px;
}

.sidebar-menu {
  padding: 1rem 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--color-text-secondary);
  font-weight: 500;
}

.menu-item:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
}

.menu-item.active {
  background: var(--color-primary);
  color: white;
  font-weight: 600;
}

.menu-item.active .el-icon {
  color: white;
}

/* 主内容区 */
.profile-content {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  padding: 2rem;
  min-height: 600px;
}

.tab-content h2 {
  margin: 0 0 2rem 0;
  color: var(--color-text-primary);
  font-size: 1.5rem;
  font-weight: 600;
}

/* 概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.overview-card {
  background: var(--color-bg-secondary);
  padding: 1.5rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all var(--transition-fast);
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.card-icon.balance {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.card-icon.bookings {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.card-icon.spending {
  background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
}

.card-icon.vip {
  background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
}

.card-content h3 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
}

.card-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.card-desc {
  margin: 0;
  color: var(--color-text-tertiary);
  font-size: 0.75rem;
}

/* 最近预订 */
.recent-bookings {
  margin-top: 2rem;
}

.recent-bookings h3 {
  margin: 0 0 1rem 0;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.booking-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.booking-info h4 {
  margin: 0 0 0.25rem 0;
  color: var(--color-text-primary);
  font-size: 0.9375rem;
  font-weight: 500;
}

.booking-date {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.75rem;
}

.booking-amount {
  font-weight: 600;
  color: var(--color-primary);
}

.no-bookings {
  text-align: center;
  padding: 2rem;
  color: var(--color-text-secondary);
}

/* 余额管理 */
.balance-overview {
  margin-bottom: 2rem;
}

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.balance-header h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  opacity: 0.9;
}

.balance-amount {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
}

.balance-actions {
  display: flex;
  gap: 1rem;
}

.balance-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  border-radius: var(--radius-md);
}

.balance-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 交易记录 */
.transaction-history h3 {
  margin: 0 0 1rem 0;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.transaction-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--color-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.transaction-info {
  flex: 1;
}

.transaction-info h4 {
  margin: 0 0 0.25rem 0;
  color: var(--color-text-primary);
  font-size: 0.9375rem;
  font-weight: 500;
}

.transaction-time {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.75rem;
}

.transaction-amount {
  font-weight: 600;
  font-size: 1rem;
}

.transaction-amount.recharge {
  color: var(--color-success);
}

.transaction-amount.payment,
.transaction-amount.withdraw {
  color: var(--color-danger);
}

/* 安全设置 */
.security-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.security-info h3 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 600;
}

.security-info p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

/* 快捷金额按钮 */
.quick-amounts {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .profile-sidebar {
    position: static;
    order: -1;
  }

  .sidebar-menu {
    display: flex;
    overflow-x: auto;
    padding: 0.5rem;
  }

  .menu-item {
    white-space: nowrap;
    padding: 0.75rem 1rem;
  }

  .overview-cards {
    grid-template-columns: 1fr;
  }

  .container {
    padding: 1rem;
  }
}
</style>