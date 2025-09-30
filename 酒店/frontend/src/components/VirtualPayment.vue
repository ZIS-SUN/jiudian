<template>
  <div class="virtual-payment">
    <el-dialog
      v-model="visible"
      title="虚拟支付"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="handleClose"
    >
      <div class="payment-content">
        <!-- 支付方式选择 -->
        <div class="payment-methods" v-if="currentStep === 'method'">
          <h3>选择支付方式</h3>
          <div class="method-list">
            <div
              class="method-item"
              :class="{ active: selectedMethod === 'WECHAT' }"
              @click="selectMethod('WECHAT')"
            >
              <div class="method-icon wechat">💳</div>
              <div class="method-info">
                <h4>微信支付</h4>
                <p>使用微信扫码支付</p>
              </div>
              <div class="method-radio">
                <el-radio :model-value="selectedMethod" label="WECHAT" />
              </div>
            </div>

            <div
              class="method-item"
              :class="{ active: selectedMethod === 'ALIPAY' }"
              @click="selectMethod('ALIPAY')"
            >
              <div class="method-icon alipay">💰</div>
              <div class="method-info">
                <h4>支付宝</h4>
                <p>使用支付宝扫码支付</p>
              </div>
              <div class="method-radio">
                <el-radio :model-value="selectedMethod" label="ALIPAY" />
              </div>
            </div>

            <div
              class="method-item"
              :class="{ active: selectedMethod === 'BALANCE' }"
              @click="selectMethod('BALANCE')"
            >
              <div class="method-icon balance">💰</div>
              <div class="method-info">
                <h4>余额支付</h4>
                <p>当前余额：¥{{ userBalance.toLocaleString() }}
                  <span v-if="!balanceEnough" class="insufficient-text">(余额不足)</span>
                </p>
              </div>
              <div class="method-radio">
                <el-radio :model-value="selectedMethod" label="BALANCE" :disabled="!balanceEnough" />
              </div>
            </div>

            <div
              class="method-item"
              :class="{ active: selectedMethod === 'BANK_CARD' }"
              @click="selectMethod('BANK_CARD')"
            >
              <div class="method-icon bank">🏦</div>
              <div class="method-info">
                <h4>银行卡支付</h4>
                <p>使用银行卡在线支付</p>
              </div>
              <div class="method-radio">
                <el-radio :model-value="selectedMethod" label="BANK_CARD" />
              </div>
            </div>
          </div>

          <div class="payment-summary">
            <div class="summary-row">
              <span>支付金额</span>
              <span class="amount">¥{{ paymentAmount.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <!-- 二维码支付 -->
        <div class="qr-payment" v-if="currentStep === 'qrcode' && (selectedMethod === 'WECHAT' || selectedMethod === 'ALIPAY')">
          <div class="qr-header">
            <div class="method-logo">
              <span v-if="selectedMethod === 'WECHAT'">💳</span>
              <span v-if="selectedMethod === 'ALIPAY'">💰</span>
            </div>
            <h3>{{ selectedMethod === 'WECHAT' ? '微信支付' : '支付宝' }}</h3>
          </div>

          <div class="qr-code">
            <div class="qr-placeholder">
              <div class="qr-grid">
                <div v-for="i in 225" :key="i" class="qr-pixel" :class="{ filled: Math.random() > 0.6 }"></div>
              </div>
            </div>
          </div>

          <div class="qr-tips">
            <p>请使用{{ selectedMethod === 'WECHAT' ? '微信' : '支付宝' }}扫描二维码完成支付</p>
            <div class="amount-display">¥{{ paymentAmount.toLocaleString() }}</div>
            <div class="countdown">支付剩余时间：{{ formatTime(countdown) }}</div>
          </div>

          <div class="payment-status">
            <el-button @click="simulatePaymentSuccess" type="success" :loading="processing">
              模拟支付成功
            </el-button>
            <el-button @click="currentStep = 'method'" type="default">
              更换支付方式
            </el-button>
          </div>
        </div>

        <!-- 余额支付 -->
        <div class="balance-payment" v-if="currentStep === 'balanceform' && selectedMethod === 'BALANCE'">
          <div class="balance-header">
            <div class="method-logo">💰</div>
            <h3>余额支付</h3>
          </div>

          <div class="balance-info">
            <div class="balance-row">
              <span>当前余额</span>
              <span class="balance-amount">¥{{ userBalance.toLocaleString() }}</span>
            </div>
            <div class="balance-row">
              <span>支付金额</span>
              <span class="pay-amount">¥{{ paymentAmount.toLocaleString() }}</span>
            </div>
            <div class="balance-row total">
              <span>支付后余额</span>
              <span class="remaining-amount">¥{{ (userBalance - paymentAmount).toLocaleString() }}</span>
            </div>
          </div>

          <div class="balance-confirm">
            <el-alert
              v-if="!balanceEnough"
              title="余额不足"
              :description="`还需充值 ¥${(paymentAmount - userBalance).toLocaleString()}`"
              type="error"
              show-icon
              :closable="false"
            />
            <el-alert
              v-else
              title="确认使用余额支付"
              type="info"
              show-icon
              :closable="false"
            />
          </div>

          <div class="balance-actions">
            <el-button @click="processBalancePayment" type="primary" :loading="processing" :disabled="!balanceEnough">
              确认支付
            </el-button>
            <el-button @click="$router.push('/profile')" v-if="!balanceEnough">
              去充值
            </el-button>
            <el-button @click="currentStep = 'method'" type="default">
              返回
            </el-button>
          </div>
        </div>

        <!-- 银行卡支付 -->
        <div class="card-payment" v-if="currentStep === 'cardform' && selectedMethod === 'BANK_CARD'">
          <div class="card-header">
            <div class="method-logo">🏦</div>
            <h3>银行卡支付</h3>
          </div>

          <el-form :model="cardForm" :rules="cardRules" ref="cardFormRef" label-width="100px">
            <el-form-item label="卡号" prop="cardNumber">
              <el-input
                v-model="cardForm.cardNumber"
                placeholder="请输入银行卡号"
                maxlength="19"
                @input="formatCardNumber"
              />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="有效期" prop="expiryDate">
                  <el-input
                    v-model="cardForm.expiryDate"
                    placeholder="MM/YY"
                    maxlength="5"
                    @input="formatExpiryDate"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="CVV" prop="cvv">
                  <el-input
                    v-model="cardForm.cvv"
                    placeholder="CVV"
                    maxlength="3"
                    show-password
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="持卡人姓名" prop="cardHolder">
              <el-input v-model="cardForm.cardHolder" placeholder="请输入持卡人姓名" />
            </el-form-item>
          </el-form>

          <div class="payment-summary">
            <div class="summary-row">
              <span>支付金额</span>
              <span class="amount">¥{{ paymentAmount.toLocaleString() }}</span>
            </div>
          </div>

          <div class="card-actions">
            <el-button @click="processCardPayment" type="primary" :loading="processing">
              确认支付
            </el-button>
            <el-button @click="currentStep = 'method'" type="default">
              返回
            </el-button>
          </div>
        </div>

        <!-- 支付成功 -->
        <div class="payment-success" v-if="currentStep === 'success'">
          <div class="success-icon">✅</div>
          <h3>支付成功</h3>
          <div class="success-details">
            <div class="detail-row">
              <span>支付方式</span>
              <span>{{ getPaymentMethodName() }}</span>
            </div>
            <div class="detail-row">
              <span>支付金额</span>
              <span class="amount">¥{{ paymentAmount.toLocaleString() }}</span>
            </div>
            <div class="detail-row">
              <span>交易时间</span>
              <span>{{ new Date().toLocaleString() }}</span>
            </div>
            <div class="detail-row">
              <span>交易号</span>
              <span>{{ transactionId }}</span>
            </div>
          </div>
        </div>

        <!-- 支付失败 -->
        <div class="payment-failure" v-if="currentStep === 'failure'">
          <div class="failure-icon">❌</div>
          <h3>支付失败</h3>
          <p>{{ failureReason || '支付过程中发生错误，请重试' }}</p>
          <div class="failure-actions">
            <el-button @click="currentStep = 'method'" type="primary">
              重新支付
            </el-button>
            <el-button @click="handleClose" type="default">
              取消支付
            </el-button>
          </div>
        </div>
      </div>

      <template #footer v-if="currentStep === 'method'">
        <div class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button
            type="primary"
            @click="proceedToPayment"
            :disabled="!selectedMethod"
            :loading="processing"
          >
            立即支付
          </el-button>
        </div>
      </template>

      <template #footer v-if="currentStep === 'success'">
        <div class="dialog-footer">
          <el-button type="primary" @click="handlePaymentComplete">
            完成
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import balanceManager from '@/utils/balanceManager'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  paymentAmount: {
    type: Number,
    required: true
  },
  orderId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'payment-success', 'payment-failure', 'payment-cancel'])

const visible = ref(false)
const currentStep = ref('method') // method, qrcode, cardform, success, failure
const selectedMethod = ref('')
const processing = ref(false)
const countdown = ref(300) // 5分钟倒计时
const transactionId = ref('')
const failureReason = ref('')
const cardFormRef = ref()

// 银行卡表单
const cardForm = reactive({
  cardNumber: '',
  expiryDate: '',
  cvv: '',
  cardHolder: ''
})

const cardRules = {
  cardNumber: [
    { required: true, message: '请输入银行卡号', trigger: 'blur' },
    { pattern: /^\d{4}\s\d{4}\s\d{4}\s\d{4}$/, message: '请输入正确的银行卡号格式', trigger: 'blur' }
  ],
  expiryDate: [
    { required: true, message: '请输入有效期', trigger: 'blur' },
    { pattern: /^\d{2}\/\d{2}$/, message: '请输入正确的有效期格式(MM/YY)', trigger: 'blur' }
  ],
  cvv: [
    { required: true, message: '请输入CVV', trigger: 'blur' },
    { pattern: /^\d{3}$/, message: 'CVV必须是3位数字', trigger: 'blur' }
  ],
  cardHolder: [
    { required: true, message: '请输入持卡人姓名', trigger: 'blur' }
  ]
}

// 余额相关数据
const userBalance = ref(0)

// 计算余额是否足够
const balanceEnough = computed(() => {
  return userBalance.value >= props.paymentAmount
})

let countdownTimer = null

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    resetPayment()
    updateUserBalance()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    clearCountdown()
  }
})

const selectMethod = (method) => {
  // 如果选择余额支付但余额不足，提示用户
  if (method === 'BALANCE' && !balanceEnough.value) {
    ElMessage.warning(`余额不足，还需充值 ¥${(props.paymentAmount - userBalance.value).toLocaleString()}`)
    return
  }
  selectedMethod.value = method
}

const proceedToPayment = () => {
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  processing.value = true

  setTimeout(() => {
    processing.value = false

    if (selectedMethod.value === 'BALANCE') {
      currentStep.value = 'balanceform'
    } else if (selectedMethod.value === 'BANK_CARD') {
      currentStep.value = 'cardform'
    } else {
      currentStep.value = 'qrcode'
      startCountdown()
    }
  }, 500)
}

// 更新用户余额
const updateUserBalance = () => {
  userBalance.value = balanceManager.getBalance()
}

// 处理余额支付
const processBalancePayment = async () => {
  if (!balanceEnough.value) {
    ElMessage.error('余额不足，请先充值')
    return
  }

  processing.value = true

  try {
    // 使用余额管理器进行支付
    const result = balanceManager.deductBalance(
      props.paymentAmount,
      `预订支付 - ${props.orderId}`
    )

    if (result.success) {
      // 支付成功，更新余额显示
      userBalance.value = result.currentBalance

      setTimeout(() => {
        processing.value = false
        handlePaymentSuccess()
      }, 1500)
    } else {
      processing.value = false
      ElMessage.error(result.message)
    }
  } catch (error) {
    processing.value = false
    ElMessage.error('支付处理失败，请重试')
  }
}

const startCountdown = () => {
  countdown.value = 300
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearCountdown()
      currentStep.value = 'failure'
      failureReason.value = '支付超时，请重新支付'
    }
  }, 1000)
}

const clearCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

const formatCardNumber = () => {
  // 格式化银行卡号为 XXXX XXXX XXXX XXXX
  let value = cardForm.cardNumber.replace(/\s/g, '').replace(/\D/g, '')
  let formattedValue = value.replace(/(\d{4})(?=\d)/g, '$1 ')
  if (formattedValue.length > 19) {
    formattedValue = formattedValue.substring(0, 19)
  }
  cardForm.cardNumber = formattedValue
}

const formatExpiryDate = () => {
  // 格式化有效期为 MM/YY
  let value = cardForm.expiryDate.replace(/\D/g, '')
  if (value.length >= 2) {
    value = value.substring(0, 2) + '/' + value.substring(2, 4)
  }
  cardForm.expiryDate = value
}

const processCardPayment = async () => {
  try {
    await cardFormRef.value.validate()
    processing.value = true

    // 模拟支付处理
    setTimeout(() => {
      processing.value = false
      // 90% 成功率
      if (Math.random() > 0.1) {
        handlePaymentSuccess()
      } else {
        currentStep.value = 'failure'
        failureReason.value = '银行卡验证失败，请检查卡号信息'
      }
    }, 2000)
  } catch (error) {
    console.log('表单验证失败')
  }
}

const simulatePaymentSuccess = () => {
  processing.value = true

  setTimeout(() => {
    processing.value = false
    clearCountdown()
    handlePaymentSuccess()
  }, 1500)
}

const handlePaymentSuccess = () => {
  transactionId.value = 'T' + Date.now().toString() + Math.random().toString(36).substr(2, 6).toUpperCase()
  currentStep.value = 'success'

  // 触发支付成功事件
  emit('payment-success', {
    paymentMethod: selectedMethod.value,
    transactionId: transactionId.value,
    amount: props.paymentAmount,
    orderId: props.orderId
  })
}

const handlePaymentComplete = () => {
  visible.value = false
  resetPayment()
}

const handleClose = () => {
  if (currentStep.value === 'success') {
    handlePaymentComplete()
  } else {
    emit('payment-cancel')
    visible.value = false
    resetPayment()
  }
}

const resetPayment = () => {
  currentStep.value = 'method'
  selectedMethod.value = ''
  processing.value = false
  transactionId.value = ''
  failureReason.value = ''
  clearCountdown()

  // 重置银行卡表单
  cardForm.cardNumber = ''
  cardForm.expiryDate = ''
  cardForm.cvv = ''
  cardForm.cardHolder = ''
}

const getPaymentMethodName = () => {
  const methodNames = {
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝',
    'BANK_CARD': '银行卡支付',
    'BALANCE': '余额支付'
  }
  return methodNames[selectedMethod.value] || '未知支付方式'
}
</script>

<style scoped>
.virtual-payment {
  /* 组件容器 */
}

/* 支付方式选择 */
.payment-methods h3 {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.method-item:hover {
  border-color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.method-item.active {
  border-color: var(--color-primary);
  background: rgba(0, 122, 255, 0.05);
}

.method-icon {
  font-size: 2rem;
  margin-right: 1rem;
  width: 50px;
  text-align: center;
}

.method-info {
  flex: 1;
}

.method-info h4 {
  margin: 0 0 0.25rem 0;
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 600;
}

.method-info p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.payment-summary {
  background: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: var(--radius-md);
  margin-bottom: 1rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-primary);
}

/* 二维码支付 */
.qr-payment {
  text-align: center;
}

.qr-header {
  margin-bottom: 2rem;
}

.method-logo {
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.qr-header h3 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 1.25rem;
  font-weight: 600;
}

.qr-code {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.qr-placeholder {
  width: 200px;
  height: 200px;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-primary);
}

.qr-grid {
  display: grid;
  grid-template-columns: repeat(15, 1fr);
  gap: 1px;
  width: 150px;
  height: 150px;
}

.qr-pixel {
  background: var(--color-bg-secondary);
  border-radius: 1px;
}

.qr-pixel.filled {
  background: var(--color-text-primary);
}

.qr-tips p {
  margin: 0 0 1rem 0;
  color: var(--color-text-secondary);
}

.amount-display {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 0.5rem;
}

.countdown {
  color: var(--color-warning);
  font-size: 0.875rem;
  margin-bottom: 2rem;
}

.payment-status {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

/* 银行卡支付 */
.card-payment {
  /* 使用默认表单样式 */
}

.card-header {
  text-align: center;
  margin-bottom: 2rem;
}

.card-header h3 {
  margin: 0.5rem 0 0 0;
  color: var(--color-text-primary);
  font-size: 1.25rem;
  font-weight: 600;
}

.card-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
}

/* 支付成功 */
.payment-success {
  text-align: center;
  padding: 2rem 0;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.payment-success h3 {
  margin: 0 0 2rem 0;
  color: var(--color-success);
  font-size: 1.5rem;
  font-weight: 600;
}

.success-details {
  background: var(--color-bg-secondary);
  padding: 1.5rem;
  border-radius: var(--radius-md);
  text-align: left;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
}

.detail-row:last-child {
  border-bottom: none;
}

/* 支付失败 */
.payment-failure {
  text-align: center;
  padding: 2rem 0;
}

.failure-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.payment-failure h3 {
  margin: 0 0 1rem 0;
  color: var(--color-danger);
  font-size: 1.5rem;
  font-weight: 600;
}

.payment-failure p {
  margin: 0 0 2rem 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.failure-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

/* 余额支付 */
.balance-payment {
  text-align: center;
}

.balance-header {
  margin-bottom: 2rem;
}

.balance-header h3 {
  margin: 0.5rem 0 0 0;
  color: var(--color-text-primary);
  font-size: 1.25rem;
  font-weight: 600;
}

.balance-info {
  background: var(--color-bg-secondary);
  padding: 1.5rem;
  border-radius: var(--radius-md);
  margin-bottom: 1.5rem;
}

.balance-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  font-size: 0.9375rem;
}

.balance-row.total {
  font-weight: 600;
  border-top: 2px solid var(--color-border);
  margin-top: 0.5rem;
  padding-top: 1rem;
}

.balance-amount {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 1.125rem;
}

.pay-amount {
  font-weight: 600;
  color: var(--color-danger);
}

.remaining-amount {
  font-weight: 600;
  color: var(--color-success);
}

.balance-confirm {
  margin-bottom: 2rem;
}

.balance-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.insufficient-text {
  color: var(--color-danger);
  font-weight: 500;
  font-size: 0.75rem;
}

.method-icon.balance {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

/* 禁用状态 */
.method-item:has(.el-radio[disabled]) {
  opacity: 0.6;
  cursor: not-allowed;
}

.method-item:has(.el-radio[disabled]):hover {
  border-color: var(--color-border);
  background: var(--color-bg-primary);
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

/* Element Plus 样式覆盖 */
:deep(.el-dialog) {
  border-radius: var(--radius-lg);
}

:deep(.el-dialog__header) {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-dialog__title) {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

:deep(.el-dialog__body) {
  padding: 1.5rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
}

:deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: 500;
}

:deep(.el-button--primary) {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

:deep(.el-button--success) {
  background: var(--color-success);
  border-color: var(--color-success);
}

:deep(.el-radio__label) {
  display: none;
}
</style>