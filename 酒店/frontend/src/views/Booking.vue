<template>
  <div class="booking-page">
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
          <router-link to="/contact" class="nav-link">联系我们</router-link>
          <router-link to="/admin" class="nav-link admin-link"
            >管理后台</router-link
          >
        </div>
      </div>
    </nav>

    <div class="page-content">
      <div class="container">
        <!-- 进度条 -->
        <div class="booking-progress">
          <div
            class="progress-step"
            :class="{ active: currentStep >= 1, completed: currentStep > 1 }"
          >
            <div class="step-number">1</div>
            <div class="step-text">填写信息</div>
          </div>
          <div class="progress-line" :class="{ active: currentStep > 1 }"></div>
          <div
            class="progress-step"
            :class="{ active: currentStep >= 2, completed: currentStep > 2 }"
          >
            <div class="step-number">2</div>
            <div class="step-text">确认订单</div>
          </div>
          <div class="progress-line" :class="{ active: currentStep > 2 }"></div>
          <div class="progress-step" :class="{ active: currentStep >= 3 }">
            <div class="step-number">3</div>
            <div class="step-text">预订成功</div>
          </div>
        </div>

        <div class="booking-content">
          <!-- 步骤1：填写信息 -->
          <div v-if="currentStep === 1" class="step-content">
            <div class="booking-layout">
              <!-- 左侧：客户信息表单 -->
              <div class="form-section">
                <h2>客户信息</h2>
                <el-form
                  ref="customerFormRef"
                  :model="customerForm"
                  :rules="customerRules"
                  label-width="100px"
                >
                  <el-form-item label="姓名" prop="name">
                    <el-input
                      v-model="customerForm.name"
                      placeholder="请输入真实姓名"
                    />
                  </el-form-item>

                  <el-form-item label="手机号" prop="phone">
                    <el-input
                      v-model="customerForm.phone"
                      placeholder="请输入手机号"
                    />
                  </el-form-item>

                  <el-form-item label="身份证号" prop="idCard">
                    <el-input
                      v-model="customerForm.idCard"
                      placeholder="请输入身份证号"
                    />
                  </el-form-item>

                  <el-form-item label="邮箱" prop="email">
                    <el-input
                      v-model="customerForm.email"
                      placeholder="请输入邮箱地址"
                    />
                  </el-form-item>

                  <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="customerForm.gender">
                      <el-radio label="MALE">男</el-radio>
                      <el-radio label="FEMALE">女</el-radio>
                    </el-radio-group>
                  </el-form-item>

                  <el-form-item label="生日" prop="birthday">
                    <el-date-picker
                      v-model="customerForm.birthday"
                      type="date"
                      placeholder="选择生日"
                      style="width: 100%"
                    />
                  </el-form-item>

                  <el-form-item label="特殊需求">
                    <el-input
                      v-model="customerForm.specialRequests"
                      type="textarea"
                      :rows="3"
                      placeholder="如需加床、无烟房间等特殊需求，请在此说明"
                    />
                  </el-form-item>
                </el-form>

                <h2 style="margin-top: 2rem">入住信息</h2>
                <el-form :model="bookingForm" label-width="100px">
                  <el-form-item label="入住日期">
                    <el-date-picker
                      v-model="bookingForm.checkInDate"
                      type="date"
                      placeholder="选择入住日期"
                      :disabled-date="disabledDate"
                      style="width: 100%"
                      @change="calculateTotal"
                    />
                  </el-form-item>

                  <el-form-item label="退房日期">
                    <el-date-picker
                      v-model="bookingForm.checkOutDate"
                      type="date"
                      placeholder="选择退房日期"
                      :disabled-date="disabledDate"
                      style="width: 100%"
                      @change="calculateTotal"
                    />
                  </el-form-item>

                  <el-form-item label="房间数量">
                    <el-input-number
                      v-model="bookingForm.roomCount"
                      :min="1"
                      :max="5"
                      @change="calculateTotal"
                    />
                  </el-form-item>

                  <el-form-item label="入住人数">
                    <el-input-number
                      v-model="bookingForm.guestCount"
                      :min="1"
                      :max="10"
                    />
                  </el-form-item>
                </el-form>
              </div>

              <!-- 右侧：订单摘要 -->
              <div class="summary-section">
                <div class="room-summary">
                  <h3>预订摘要</h3>

                  <div class="selected-room">
                    <img :src="selectedRoom.image" :alt="selectedRoom.name" />
                    <div class="room-info">
                      <h4>{{ selectedRoom.name }}</h4>
                      <p>{{ selectedRoom.description }}</p>
                      <div class="room-amenities">
                        <span
                          v-for="amenity in selectedRoom.amenities.slice(0, 3)"
                          :key="amenity"
                          class="amenity"
                        >
                          {{ amenity }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="booking-details">
                    <div class="detail-row">
                      <span>入住日期</span>
                      <span>{{ formatDate(bookingForm.checkInDate) }}</span>
                    </div>
                    <div class="detail-row">
                      <span>退房日期</span>
                      <span>{{ formatDate(bookingForm.checkOutDate) }}</span>
                    </div>
                    <div class="detail-row">
                      <span>住宿天数</span>
                      <span>{{ totalNights }}晚</span>
                    </div>
                    <div class="detail-row">
                      <span>房间数量</span>
                      <span>{{ bookingForm.roomCount }}间</span>
                    </div>
                    <div class="detail-row">
                      <span>入住人数</span>
                      <span>{{ bookingForm.guestCount }}人</span>
                    </div>
                  </div>

                  <div class="price-breakdown">
                    <div class="price-row">
                      <span>房费</span>
                      <span
                        >¥{{ selectedRoom.price }} × {{ totalNights }}晚 ×
                        {{ bookingForm.roomCount }}间</span
                      >
                      <span>¥{{ roomTotal.toLocaleString() }}</span>
                    </div>
                    <div class="price-row">
                      <span>服务费</span>
                      <span></span>
                      <span>¥{{ serviceFee.toLocaleString() }}</span>
                    </div>
                    <div class="price-row">
                      <span>税费</span>
                      <span></span>
                      <span>¥{{ taxFee.toLocaleString() }}</span>
                    </div>
                    <div class="price-row total">
                      <span>总计</span>
                      <span></span>
                      <span>¥{{ totalAmount.toLocaleString() }}</span>
                    </div>
                  </div>

                  <div class="booking-actions">
                    <el-button size="large" @click="goBack">返回</el-button>
                    <el-button
                      type="primary"
                      size="large"
                      @click="nextStep"
                      :loading="submitting"
                    >
                      下一步
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤2：确认订单 -->
          <div v-if="currentStep === 2" class="step-content">
            <div class="confirmation-layout">
              <div class="confirmation-details">
                <h2>确认预订信息</h2>

                <div class="info-section">
                  <h3>客户信息</h3>
                  <div class="info-grid">
                    <div class="info-item">
                      <span class="label">姓名</span>
                      <span class="value">{{ customerForm.name }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">手机号</span>
                      <span class="value">{{ customerForm.phone }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">身份证号</span>
                      <span class="value">{{
                        maskIdCard(customerForm.idCard)
                      }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">邮箱</span>
                      <span class="value">{{ customerForm.email }}</span>
                    </div>
                  </div>
                </div>

                <div class="info-section">
                  <h3>预订信息</h3>
                  <div class="booking-summary-card">
                    <div class="room-preview">
                      <img :src="selectedRoom.image" :alt="selectedRoom.name" />
                      <div>
                        <h4>{{ selectedRoom.name }}</h4>
                        <p>
                          {{ formatDate(bookingForm.checkInDate) }} -
                          {{ formatDate(bookingForm.checkOutDate) }}
                        </p>
                        <p>
                          {{ bookingForm.roomCount }}间房 ·
                          {{ bookingForm.guestCount }}位客人 ·
                          {{ totalNights }}晚
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="info-section">
                  <h3>费用明细</h3>
                  <div class="final-price-breakdown">
                    <div class="price-item">
                      <span
                        >房费 ({{ totalNights }}晚 ×
                        {{ bookingForm.roomCount }}间)</span
                      >
                      <span>¥{{ roomTotal.toLocaleString() }}</span>
                    </div>
                    <div class="price-item">
                      <span>服务费</span>
                      <span>¥{{ serviceFee.toLocaleString() }}</span>
                    </div>
                    <div class="price-item">
                      <span>税费</span>
                      <span>¥{{ taxFee.toLocaleString() }}</span>
                    </div>
                    <div class="price-item total">
                      <span>应付总额</span>
                      <span>¥{{ totalAmount.toLocaleString() }}</span>
                    </div>
                  </div>
                </div>

                <div class="info-section">
                  <h3>支付方式</h3>
                  <el-radio-group
                    v-model="paymentMethod"
                    class="payment-methods"
                  >
                    <el-radio label="WECHAT" class="payment-option">
                      <div class="payment-content">
                        <span class="payment-icon">💳</span>
                        <span>微信支付</span>
                      </div>
                    </el-radio>
                    <el-radio label="ALIPAY" class="payment-option">
                      <div class="payment-content">
                        <span class="payment-icon">💰</span>
                        <span>支付宝</span>
                      </div>
                    </el-radio>
                    <el-radio label="BANK_CARD" class="payment-option">
                      <div class="payment-content">
                        <span class="payment-icon">🏦</span>
                        <span>银行卡支付</span>
                      </div>
                    </el-radio>
                  </el-radio-group>
                </div>

                <div class="confirmation-actions">
                  <el-button size="large" @click="prevStep">上一步</el-button>
                  <el-button
                    type="primary"
                    size="large"
                    @click="showPaymentDialog"
                    :loading="submitting"
                  >
                    确认预订并支付
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤3：预订成功 -->
          <div v-if="currentStep === 3" class="step-content">
            <div class="success-layout">
              <div class="success-content">
                <div class="success-icon">✅</div>
                <h2>预订成功！</h2>
                <p>您的预订已确认，我们会在24小时内与您联系确认详细信息。</p>

                <div class="booking-number">
                  <span>预订号：</span>
                  <span class="number">{{ bookingNumber }}</span>
                </div>

                <div class="success-details">
                  <div class="detail-card">
                    <h4>{{ selectedRoom.name }}</h4>
                    <p>
                      {{ formatDate(bookingForm.checkInDate) }} -
                      {{ formatDate(bookingForm.checkOutDate) }}
                    </p>
                    <p>{{ bookingForm.roomCount }}间房 · {{ totalNights }}晚</p>
                    <p class="amount">
                      支付金额：¥{{ totalAmount.toLocaleString() }}
                    </p>
                  </div>
                </div>

                <div class="success-actions">
                  <el-button size="large" @click="viewBookings"
                    >查看我的预订</el-button
                  >
                  <el-button type="primary" size="large" @click="goHome"
                    >返回首页</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 虚拟支付弹窗 -->
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
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import VirtualPayment from "@/components/VirtualPayment.vue";
import { useUserStore } from "@/stores/user";
import { mockBookings } from "@/data/mockData";
import { createReservation } from "@/api/reservations";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 状态管理
const currentStep = ref(1);
const submitting = ref(false);
const customerFormRef = ref();
const paymentMethod = ref("WECHAT");
const bookingNumber = ref("");
const showPayment = ref(false);

// 表单数据
const customerForm = ref({
  name: "",
  phone: "",
  idCard: "",
  email: "",
  gender: "MALE",
  birthday: "",
  specialRequests: "",
});

const bookingForm = ref({
  checkInDate: "",
  checkOutDate: "",
  roomCount: 1,
  guestCount: 2,
});

// 选中的房间信息（模拟数据）
const selectedRoom = ref({
  id: 1,
  name: "豪华海景大床房",
  description: "宽敞舒适的豪华客房，配备大床和现代化设施",
  price: 688,
  image:
    "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&h=300&fit=crop",
  amenities: ["免费WiFi", "空调", "迷你吧", "24小时服务", "海景阳台", "浴缸"],
});

// 表单验证规则
const customerRules = {
  name: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    { min: 2, max: 20, message: "姓名长度应为2-20个字符", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  idCard: [
    { required: true, message: "请输入身份证号", trigger: "blur" },
    {
      pattern:
        /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/,
      message: "请输入正确的身份证号",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
};

// 计算属性
const totalNights = computed(() => {
  if (!bookingForm.value.checkInDate || !bookingForm.value.checkOutDate)
    return 0;
  const checkIn = new Date(bookingForm.value.checkInDate);
  const checkOut = new Date(bookingForm.value.checkOutDate);
  const diffTime = checkOut.getTime() - checkIn.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays > 0 ? diffDays : 0;
});

const roomTotal = computed(() => {
  return (
    selectedRoom.value.price * totalNights.value * bookingForm.value.roomCount
  );
});

const serviceFee = computed(() => {
  return Math.round(roomTotal.value * 0.05); // 5% 服务费
});

const taxFee = computed(() => {
  return Math.round(roomTotal.value * 0.03); // 3% 税费
});

const totalAmount = computed(() => {
  return roomTotal.value + serviceFee.value + taxFee.value;
});

// 方法
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

const maskIdCard = (idCard) => {
  if (!idCard) return "";
  return idCard.replace(/^(.{6}).*(.{4})$/, "$1******$2");
};

const calculateTotal = () => {
  // 触发重新计算
};

const goBack = () => {
  router.go(-1);
};

const nextStep = async () => {
  if (!customerFormRef.value) return;

  try {
    await customerFormRef.value.validate();

    if (!bookingForm.value.checkInDate || !bookingForm.value.checkOutDate) {
      ElMessage.warning("请选择入住和退房日期");
      return;
    }

    if (totalNights.value <= 0) {
      ElMessage.warning("退房日期必须晚于入住日期");
      return;
    }

    currentStep.value = 2;
    window.scrollTo({ top: 0, behavior: "smooth" });
  } catch (error) {
    ElMessage.error("请完善客户信息");
  }
};

const prevStep = () => {
  currentStep.value = 1;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const showPaymentDialog = () => {
  // 生成预订号
  bookingNumber.value = "HT" + Date.now().toString().slice(-8);
  showPayment.value = true;
};

const handlePaymentSuccess = async (paymentInfo) => {
  try {
    // 保存预订信息到后端和本地存储
    await saveBookingData(paymentInfo);

    currentStep.value = 3;
    window.scrollTo({ top: 0, behavior: "smooth" });

    ElMessage.success("支付成功，预订完成！");
  } catch (error) {
    // 如果保存失败，停留在当前步骤
    console.error('处理支付成功时出错:', error);
  }
};

const handlePaymentFailure = () => {
  ElMessage.error("支付失败，请重试");
};

const handlePaymentCancel = () => {
  ElMessage.info("已取消支付");
};

const saveBookingData = async (paymentInfo) => {
  const userInfo = userStore.userInfo;

  try {
    // 准备预订数据
    const reservationData = {
      orderId: bookingNumber.value,
      customer: {
        name: customerForm.value.name,
        phone: customerForm.value.phone,
        email: customerForm.value.email,
        idCard: customerForm.value.idCard,
        gender: customerForm.value.gender,
        birthday: customerForm.value.birthday
      },
      roomId: selectedRoom.value.id,
      checkInDate: bookingForm.value.checkInDate,
      checkOutDate: bookingForm.value.checkOutDate,
      adultsCount: bookingForm.value.guestCount,
      childrenCount: 0,
      specialRequests: customerForm.value.specialRequests || '',
      totalPrice: totalAmount.value,
      deposit: Math.round(totalAmount.value * 0.3), // 30%押金
      paymentMethod: paymentInfo.paymentMethod,
      transactionId: paymentInfo.transactionId
    };

    // 调用后端API创建预订
    const response = await createReservation(reservationData);

    if (response.code === 200) {
      // 成功创建预订
      const newBooking = {
        id: response.data.id,
        bookingNumber: bookingNumber.value,
        userId: userInfo?.id || Date.now(),
        userName: customerForm.value.name,
        userPhone: customerForm.value.phone,
        userEmail: customerForm.value.email,
        roomTypeId: selectedRoom.value.id,
        roomTypeName: selectedRoom.value.name,
        roomNumber: response.data.roomNumber || '',
        checkInDate: bookingForm.value.checkInDate,
        checkOutDate: bookingForm.value.checkOutDate,
        nights: totalNights.value,
        roomCount: bookingForm.value.roomCount,
        guestCount: bookingForm.value.guestCount,
        totalAmount: totalAmount.value,
        paidAmount: totalAmount.value,
        status: 'confirmed',
        paymentStatus: 'paid',
        paymentMethod: paymentInfo.paymentMethod,
        bookingTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
        specialRequests: customerForm.value.specialRequests || '',
        source: 'online',
        rating: null,
        review: null,
        checkInTime: null,
        checkOutTime: null,
        transactionId: paymentInfo.transactionId
      };

      // 同时保存到本地存储用于前端展示
      mockBookings.push(newBooking);
      const existingBookings = JSON.parse(localStorage.getItem('userBookings') || '[]');
      existingBookings.push(newBooking);
      localStorage.setItem('userBookings', JSON.stringify(existingBookings));
    } else {
      throw new Error(response.message || '创建预订失败');
    }
  } catch (error) {
    console.error('保存预订失败:', error);
    ElMessage.error('预订失败，请稍后重试');
    throw error;
  }
};

const viewBookings = () => {
  router.push("/my-bookings");
};

const goHome = () => {
  router.push("/");
};

// 初始化
onMounted(() => {
  // 从路由参数获取预订信息
  if (route.query.roomId) {
    selectedRoom.value.id = route.query.roomId;
  }
  if (route.query.roomName) {
    selectedRoom.value.name = route.query.roomName;
  }
  if (route.query.price) {
    selectedRoom.value.price = Number(route.query.price);
  }
  if (route.query.checkIn) {
    bookingForm.value.checkInDate = route.query.checkIn;
  }
  if (route.query.checkOut) {
    bookingForm.value.checkOutDate = route.query.checkOut;
  }
  if (route.query.rooms) {
    bookingForm.value.roomCount = Number(route.query.rooms);
  }
});
</script>

<style scoped>
.booking-page {
  min-height: 100vh;
  background: var(--color-bg-secondary);
}

/* 导航栏 - Apple Design */
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

.nav-link:hover {
  color: var(--color-primary);
  opacity: 1;
}

.admin-link {
  background: var(--color-text-primary) !important;
  color: var(--color-bg-primary) !important;
  padding: 0.5rem 1.25rem !important;
  border-radius: var(--radius-xl) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;
  transition: all var(--transition-fast) !important;
  opacity: 1 !important;
}

.admin-link:hover {
  background: var(--color-text-secondary) !important;
}

.page-content {
  padding-top: 76px;
  padding-bottom: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 进度条 */
.booking-progress {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  padding: 2rem;
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--color-bg-secondary);
  color: var(--color-text-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-bottom: 0.5rem;
  transition: all var(--transition-fast);
  font-size: 1rem;
}

.progress-step.active .step-number {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
}

.progress-step.completed .step-number {
  background: var(--color-success);
  color: white;
}

.step-text {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  white-space: nowrap;
}

.progress-step.active .step-text {
  color: var(--color-text-primary);
  font-weight: 500;
}

.progress-line {
  width: 100px;
  height: 2px;
  background: var(--color-border);
  margin: 0 1rem;
  margin-top: -20px;
  transition: all var(--transition-fast);
}

.progress-line.active {
  background: var(--color-primary);
}

/* 预订内容 */
.booking-content {
  background: var(--color-bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.step-content {
  padding: 2rem;
}

/* 步骤1：布局 */
.booking-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
}

.form-section h2 {
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
  font-size: 1.375rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.summary-section {
  position: sticky;
  top: 120px;
  height: fit-content;
}

.room-summary {
  background: var(--color-bg-secondary);
  padding: 2rem;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
}

.room-summary h3 {
  color: var(--color-text-primary);
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 1.125rem;
  font-weight: 600;
}

.selected-room {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: var(--color-bg-primary);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.selected-room img {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.room-info h4 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 600;
}

.room-info p {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
}

.room-amenities {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
}

.amenity {
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
  padding: 0.25rem 0.625rem;
  border-radius: var(--radius-xl);
  font-size: 0.75rem;
  font-weight: 500;
}

.booking-details,
.price-breakdown {
  margin-bottom: 2rem;
}

.detail-row,
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9375rem;
}

.detail-row:last-child,
.price-row:last-child {
  border-bottom: none;
}

.price-row.total {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 1.125rem;
  border-top: 2px solid var(--color-primary);
  margin-top: 1rem;
  padding-top: 1rem;
}

.booking-actions {
  display: flex;
  gap: 1rem;
}

.booking-actions .el-button {
  flex: 1;
}

/* 步骤2：确认订单 */
.confirmation-layout {
  max-width: 800px;
  margin: 0 auto;
}

.confirmation-details h2 {
  color: #2c5aa0;
  margin-bottom: 2rem;
  text-align: center;
}

.info-section {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 10px;
}

.info-section h3 {
  color: #2c5aa0;
  margin-bottom: 1rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #333;
}

.booking-summary-card {
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
}

.room-preview {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.room-preview img {
  width: 100px;
  height: 75px;
  border-radius: 8px;
  object-fit: cover;
}

.room-preview h4 {
  margin: 0 0 0.5rem 0;
  color: #2c5aa0;
}

.room-preview p {
  margin: 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.final-price-breakdown {
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #eee;
}

.price-item.total {
  font-weight: 600;
  color: #2c5aa0;
  font-size: 1.2rem;
  border-top: 2px solid #2c5aa0;
  border-bottom: none;
  margin-top: 1rem;
  padding-top: 1rem;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.payment-option {
  background: white;
  padding: 1rem;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.payment-option:hover {
  border-color: #2c5aa0;
}

.payment-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.payment-icon {
  font-size: 1.5rem;
}

.confirmation-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
}

/* 步骤3：成功页面 */
.success-layout {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
  padding: 3rem 2rem;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.success-content h2 {
  color: #4caf50;
  margin-bottom: 1rem;
}

.success-content p {
  color: #666;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.booking-number {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 10px;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.booking-number .number {
  font-weight: 600;
  color: #2c5aa0;
}

.success-details {
  margin-bottom: 2rem;
}

.detail-card {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 10px;
  border-left: 4px solid #4caf50;
}

.detail-card h4 {
  color: #2c5aa0;
  margin-bottom: 0.5rem;
}

.detail-card p {
  margin: 0.25rem 0;
  color: #666;
}

.detail-card .amount {
  font-weight: 600;
  color: #2c5aa0;
  font-size: 1.1rem;
}

.success-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .booking-layout {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .summary-section {
    position: static;
    order: -1;
  }

  .booking-progress {
    padding: 1rem;
  }

  .progress-line {
    width: 60px;
  }

  .step-text {
    font-size: 0.8rem;
  }

  .booking-actions,
  .confirmation-actions,
  .success-actions {
    flex-direction: column;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .room-preview {
    flex-direction: column;
    text-align: center;
  }

  .payment-methods {
    gap: 0.5rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }

  .step-content {
    padding: 1rem;
  }

  .room-summary {
    padding: 1rem;
  }
}
</style>
