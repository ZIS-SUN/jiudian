<template>
  <div class="attraction-booking">
    <el-dialog
      v-model="visible"
      :title="`预订 ${attraction?.name || ''}`"
      width="600px"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <div class="booking-content" v-if="attraction">
        <!-- 景点信息 -->
        <div class="attraction-info">
          <img :src="attraction.image" :alt="attraction.name" class="attraction-image" />
          <div class="attraction-details">
            <h3>{{ attraction.name }}</h3>
            <p>{{ attraction.description }}</p>
            <div class="attraction-meta">
              <div class="meta-item">
                <span class="label">📍 距离：</span>
                <span class="value">{{ attraction.distance }}</span>
              </div>
              <div class="meta-item">
                <span class="label">⭐ 评分：</span>
                <span class="value">{{ attraction.rating }}分 ({{ attraction.reviewCount }}条评价)</span>
              </div>
              <div class="meta-item">
                <span class="label">🎫 门票：</span>
                <span class="value price">{{ attraction.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 预订表单 -->
        <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="游览日期" prop="visitDate">
                <el-date-picker
                  v-model="bookingForm.visitDate"
                  type="date"
                  placeholder="选择游览日期"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="时间段" prop="timeSlot">
                <el-select v-model="bookingForm.timeSlot" placeholder="选择时间段" style="width: 100%">
                  <el-option label="上午场 (09:00-12:00)" value="morning" />
                  <el-option label="下午场 (13:00-17:00)" value="afternoon" />
                  <el-option label="全天 (09:00-17:00)" value="fullday" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="成人票" prop="adultTickets">
                <el-input-number
                  v-model="bookingForm.adultTickets"
                  :min="0"
                  :max="20"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="儿童票" prop="childTickets">
                <el-input-number
                  v-model="bookingForm.childTickets"
                  :min="0"
                  :max="20"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学生票" prop="studentTickets">
                <el-input-number
                  v-model="bookingForm.studentTickets"
                  :min="0"
                  :max="20"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="联系人姓名" prop="contactName">
            <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
          </el-form-item>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>

          <el-form-item label="备注">
            <el-input
              v-model="bookingForm.notes"
              type="textarea"
              :rows="3"
              placeholder="特殊需求或备注信息"
            />
          </el-form-item>
        </el-form>

        <!-- 价格汇总 -->
        <div class="price-summary">
          <h4>费用明细</h4>
          <div class="price-breakdown">
            <div class="price-row" v-if="bookingForm.adultTickets > 0">
              <span>成人票 × {{ bookingForm.adultTickets }}</span>
              <span>¥{{ (adultPrice * bookingForm.adultTickets).toLocaleString() }}</span>
            </div>
            <div class="price-row" v-if="bookingForm.childTickets > 0">
              <span>儿童票 × {{ bookingForm.childTickets }}</span>
              <span>¥{{ (childPrice * bookingForm.childTickets).toLocaleString() }}</span>
            </div>
            <div class="price-row" v-if="bookingForm.studentTickets > 0">
              <span>学生票 × {{ bookingForm.studentTickets }}</span>
              <span>¥{{ (studentPrice * bookingForm.studentTickets).toLocaleString() }}</span>
            </div>
            <div class="price-row total">
              <span>总计</span>
              <span>¥{{ totalAmount.toLocaleString() }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button
            type="primary"
            @click="submitBooking"
            :loading="submitting"
            :disabled="totalAmount === 0"
          >
            立即预订 (¥{{ totalAmount.toLocaleString() }})
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 支付弹窗 -->
    <VirtualPayment
      v-model="showPayment"
      :payment-amount="totalAmount"
      :order-id="bookingNumber"
      @payment-success="handlePaymentSuccess"
      @payment-failure="handlePaymentFailure"
      @payment-cancel="handlePaymentCancel"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import VirtualPayment from './VirtualPayment.vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  attraction: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'booking-success'])

const userStore = useUserStore()
const visible = ref(false)
const submitting = ref(false)
const showPayment = ref(false)
const bookingFormRef = ref()
const bookingNumber = ref('')

// 票价配置
const adultPrice = ref(60)  // 成人票价
const childPrice = ref(30)  // 儿童票价（半价）
const studentPrice = ref(45) // 学生票价（75折）

const bookingForm = reactive({
  visitDate: '',
  timeSlot: '',
  adultTickets: 1,
  childTickets: 0,
  studentTickets: 0,
  contactName: '',
  contactPhone: '',
  notes: ''
})

const bookingRules = {
  visitDate: [
    { required: true, message: '请选择游览日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应为2-20个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 计算总价
const totalAmount = computed(() => {
  return adultPrice.value * bookingForm.adultTickets +
         childPrice.value * bookingForm.childTickets +
         studentPrice.value * bookingForm.studentTickets
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.attraction) {
    initializeForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.attraction, (attraction) => {
  if (attraction) {
    // 根据景点价格调整票价
    const basePrice = parseFloat(attraction.price.replace('¥', '').replace('起', '')) || 60
    adultPrice.value = basePrice
    childPrice.value = Math.round(basePrice * 0.5)
    studentPrice.value = Math.round(basePrice * 0.75)
  }
})

const initializeForm = () => {
  // 预填写用户信息
  const userInfo = userStore.userInfo
  if (userInfo) {
    bookingForm.contactName = userInfo.name || userInfo.username || ''
    bookingForm.contactPhone = userInfo.phone || ''
  }

  // 设置最早可选日期为明天
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  bookingForm.visitDate = tomorrow
}

const disabledDate = (time) => {
  // 禁用今天之前的日期
  return time.getTime() < Date.now() - 8.64e7
}

const calculateTotal = () => {
  // 总价会自动通过computed计算
}

const submitBooking = async () => {
  try {
    await bookingFormRef.value.validate()

    if (totalAmount.value === 0) {
      ElMessage.warning('请至少选择一张门票')
      return
    }

    // 生成订单号
    bookingNumber.value = 'AT' + Date.now().toString().slice(-8)

    // 显示支付弹窗
    showPayment.value = true

  } catch (error) {
    console.log('表单验证失败')
  }
}

const handlePaymentSuccess = (paymentInfo) => {
  // 保存景点预订信息
  saveAttractionBooking(paymentInfo)

  ElMessage.success('景点门票预订成功！')
  emit('booking-success', {
    attraction: props.attraction,
    booking: bookingForm,
    payment: paymentInfo,
    orderNumber: bookingNumber.value
  })

  handleClose()
}

const handlePaymentFailure = () => {
  ElMessage.error('支付失败，请重试')
}

const handlePaymentCancel = () => {
  ElMessage.info('已取消支付')
}

const saveAttractionBooking = (paymentInfo) => {
  const bookingData = {
    id: bookingNumber.value,
    bookingNumber: bookingNumber.value,
    type: 'attraction',
    attractionId: props.attraction.id,
    attractionName: props.attraction.name,
    roomTypeName: `景点门票 - ${props.attraction.name}`,
    checkInDate: bookingForm.visitDate,
    checkOutDate: bookingForm.visitDate,
    nights: 0,
    roomCount: 1,
    guestCount: bookingForm.adultTickets + bookingForm.childTickets + bookingForm.studentTickets,
    visitDate: bookingForm.visitDate,
    timeSlot: bookingForm.timeSlot,
    tickets: {
      adult: bookingForm.adultTickets,
      child: bookingForm.childTickets,
      student: bookingForm.studentTickets
    },
    userName: bookingForm.contactName,
    userPhone: bookingForm.contactPhone,
    userEmail: userStore.userInfo?.email || '',
    specialRequests: bookingForm.notes,
    totalAmount: totalAmount.value,
    paymentMethod: paymentInfo.paymentMethod,
    paymentStatus: 'paid',
    transactionId: paymentInfo.transactionId,
    status: 'confirmed',
    bookingTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }

  // 保存到userBookings，这样MyBookings页面可以读取到
  const existingBookings = JSON.parse(localStorage.getItem('userBookings') || '[]')
  existingBookings.push(bookingData)
  localStorage.setItem('userBookings', JSON.stringify(existingBookings))

  // 同时保存到attractionBookings以便景点相关的查询
  const attractionBookings = JSON.parse(localStorage.getItem('attractionBookings') || '[]')
  attractionBookings.push(bookingData)
  localStorage.setItem('attractionBookings', JSON.stringify(attractionBookings))
}

const handleClose = () => {
  visible.value = false
  resetForm()
}

const resetForm = () => {
  bookingForm.visitDate = ''
  bookingForm.timeSlot = ''
  bookingForm.adultTickets = 1
  bookingForm.childTickets = 0
  bookingForm.studentTickets = 0
  bookingForm.contactName = ''
  bookingForm.contactPhone = ''
  bookingForm.notes = ''
  bookingFormRef.value?.clearValidate()
}
</script>

<style scoped>
.attraction-booking {
  /* 组件容器 */
}

.booking-content {
  max-height: 70vh;
  overflow-y: auto;
}

/* 景点信息 */
.attraction-info {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}

.attraction-image {
  width: 120px;
  height: 90px;
  border-radius: var(--radius-sm);
  object-fit: cover;
  flex-shrink: 0;
}

.attraction-details {
  flex: 1;
}

.attraction-details h3 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  font-weight: 600;
}

.attraction-details p {
  margin: 0 0 1rem 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
}

.attraction-meta {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 0.875rem;
}

.label {
  color: var(--color-text-tertiary);
  margin-right: 0.5rem;
}

.value {
  color: var(--color-text-primary);
}

.value.price {
  color: var(--color-primary);
  font-weight: 600;
}

/* 价格汇总 */
.price-summary {
  margin-top: 2rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}

.price-summary h4 {
  margin: 0 0 1rem 0;
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 600;
}

.price-breakdown {
  /* 价格明细样式 */
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9375rem;
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

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper) {
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

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-date-editor) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .attraction-info {
    flex-direction: column;
    text-align: center;
  }

  .attraction-image {
    width: 100%;
    height: 150px;
    align-self: center;
  }

  .attraction-meta {
    justify-content: center;
  }

  .meta-item {
    justify-content: center;
  }
}
</style>