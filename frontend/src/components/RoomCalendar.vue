<template>
  <div class="room-calendar">
    <div class="calendar-header">
      <h3>房间可用性日历</h3>
      <div class="calendar-controls">
        <button @click="previousMonth" class="cal-btn">
          <svg width="20" height="20" viewBox="0 0 20 20">
            <path d="M12 5L7 10L12 15" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round"/>
          </svg>
        </button>
        <div class="month-display">
          {{ currentMonthYear }}
        </div>
        <button @click="nextMonth" class="cal-btn">
          <svg width="20" height="20" viewBox="0 0 20 20">
            <path d="M8 5L13 10L8 15" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round"/>
          </svg>
        </button>
        <button @click="goToToday" class="cal-btn today-btn">今天</button>
      </div>
    </div>

    <div class="calendar-legend">
      <div class="legend-item">
        <span class="legend-color available"></span>
        <span>可预订</span>
      </div>
      <div class="legend-item">
        <span class="legend-color partial"></span>
        <span>部分可订</span>
      </div>
      <div class="legend-item">
        <span class="legend-color booked"></span>
        <span>已满房</span>
      </div>
      <div class="legend-item">
        <span class="legend-color selected"></span>
        <span>已选择</span>
      </div>
    </div>

    <div class="calendar-grid">
      <div class="weekdays">
        <div v-for="day in weekDays" :key="day" class="weekday">
          {{ day }}
        </div>
      </div>

      <div class="days">
        <div
          v-for="day in calendarDays"
          :key="day.date"
          class="day"
          :class="{
            'other-month': !day.isCurrentMonth,
            'today': day.isToday,
            'selected': isSelected(day.date),
            'in-range': isInRange(day.date),
            'available': day.availability === 'available',
            'partial': day.availability === 'partial',
            'booked': day.availability === 'booked',
            'past': day.isPast
          }"
          @click="selectDate(day)"
        >
          <div class="day-number">{{ day.day }}</div>
          <div class="day-price" v-if="day.price && day.isCurrentMonth && !day.isPast">
            ¥{{ day.price }}
          </div>
          <div class="day-rooms" v-if="day.isCurrentMonth && !day.isPast && day.roomsLeft">
            剩{{ day.roomsLeft }}间
          </div>
        </div>
      </div>
    </div>

    <div class="calendar-footer" v-if="selectedDates.length > 0">
      <div class="selection-info">
        <div class="selection-dates">
          <div class="date-item">
            <span class="label">入住：</span>
            <span class="date">{{ formatDate(selectedDates[0]) }}</span>
          </div>
          <div class="date-item" v-if="selectedDates[1]">
            <span class="label">退房：</span>
            <span class="date">{{ formatDate(selectedDates[1]) }}</span>
          </div>
        </div>
        <div class="selection-summary" v-if="selectedDates[1]">
          <span class="nights">{{ nightCount }}晚</span>
          <span class="total">总计：¥{{ totalPrice }}</span>
        </div>
      </div>
      <div class="selection-actions">
        <button @click="clearSelection" class="btn-clear">清除</button>
        <button @click="confirmSelection" class="btn-confirm">确认选择</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['date-selected', 'date-range-selected'])

const props = defineProps({
  roomTypeId: {
    type: [String, Number],
    default: null
  }
})

const currentDate = ref(new Date())
const selectedDates = ref([])
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 生成日历数据
const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const prevLastDay = new Date(year, month, 0)

  const daysInMonth = lastDay.getDate()
  const startingDayOfWeek = firstDay.getDay()
  const daysInPrevMonth = prevLastDay.getDate()

  const days = []
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  // 上月末尾日期
  for (let i = startingDayOfWeek - 1; i >= 0; i--) {
    const day = daysInPrevMonth - i
    const date = new Date(year, month - 1, day)
    days.push({
      day,
      date: date.toISOString().split('T')[0],
      isCurrentMonth: false,
      isToday: false,
      isPast: date < today,
      availability: 'available',
      price: null,
      roomsLeft: null
    })
  }

  // 当月日期
  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(year, month, day)
    const dateStr = date.toISOString().split('T')[0]
    const isPast = date < today

    // 模拟可用性数据
    let availability = 'available'
    let price = 588
    let roomsLeft = Math.floor(Math.random() * 10) + 1

    if (!isPast) {
      const random = Math.random()
      if (random < 0.1) {
        availability = 'booked'
        roomsLeft = 0
      } else if (random < 0.3) {
        availability = 'partial'
        roomsLeft = Math.floor(Math.random() * 3) + 1
      }

      // 周末价格上涨
      if (date.getDay() === 0 || date.getDay() === 6) {
        price = 788
      }

      // 特殊日期价格
      if (day === 15 || day === 30) {
        price = 888
      }
    }

    days.push({
      day,
      date: dateStr,
      isCurrentMonth: true,
      isToday: date.getTime() === today.getTime(),
      isPast,
      availability: isPast ? 'past' : availability,
      price: isPast ? null : price,
      roomsLeft: isPast ? null : roomsLeft
    })
  }

  // 下月开头日期
  const remainingDays = 42 - days.length
  for (let day = 1; day <= remainingDays; day++) {
    const date = new Date(year, month + 1, day)
    days.push({
      day,
      date: date.toISOString().split('T')[0],
      isCurrentMonth: false,
      isToday: false,
      isPast: date < today,
      availability: 'available',
      price: null,
      roomsLeft: null
    })
  }

  return days
})

const currentMonthYear = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth() + 1
  return `${year}年${month}月`
})

const nightCount = computed(() => {
  if (selectedDates.value.length === 2) {
    const start = new Date(selectedDates.value[0])
    const end = new Date(selectedDates.value[1])
    return Math.ceil((end - start) / (1000 * 60 * 60 * 24))
  }
  return 0
})

const totalPrice = computed(() => {
  if (selectedDates.value.length === 2) {
    // 计算选中日期范围内的总价
    let total = 0
    const start = new Date(selectedDates.value[0])
    const end = new Date(selectedDates.value[1])

    for (let d = new Date(start); d < end; d.setDate(d.getDate() + 1)) {
      const dateStr = d.toISOString().split('T')[0]
      const dayData = calendarDays.value.find(day => day.date === dateStr)
      if (dayData && dayData.price) {
        total += dayData.price
      }
    }

    return total
  }
  return 0
})

const previousMonth = () => {
  currentDate.value = new Date(
    currentDate.value.getFullYear(),
    currentDate.value.getMonth() - 1,
    1
  )
}

const nextMonth = () => {
  currentDate.value = new Date(
    currentDate.value.getFullYear(),
    currentDate.value.getMonth() + 1,
    1
  )
}

const goToToday = () => {
  currentDate.value = new Date()
}

const selectDate = (day) => {
  if (day.isPast || !day.isCurrentMonth || day.availability === 'booked') {
    return
  }

  if (selectedDates.value.length === 0) {
    selectedDates.value = [day.date]
  } else if (selectedDates.value.length === 1) {
    if (new Date(day.date) > new Date(selectedDates.value[0])) {
      selectedDates.value.push(day.date)
    } else if (day.date === selectedDates.value[0]) {
      selectedDates.value = []
    } else {
      selectedDates.value = [day.date]
    }
  } else {
    selectedDates.value = [day.date]
  }
}

const isSelected = (date) => {
  return selectedDates.value.includes(date)
}

const isInRange = (date) => {
  if (selectedDates.value.length !== 2) return false
  const d = new Date(date)
  const start = new Date(selectedDates.value[0])
  const end = new Date(selectedDates.value[1])
  return d > start && d < end
}

const clearSelection = () => {
  selectedDates.value = []
}

const confirmSelection = () => {
  if (selectedDates.value.length === 2) {
    emit('date-range-selected', {
      checkIn: selectedDates.value[0],
      checkOut: selectedDates.value[1],
      nights: nightCount.value,
      totalPrice: totalPrice.value
    })
    ElMessage.success('日期选择成功！')
  } else {
    ElMessage.warning('请选择入住和退房日期')
  }
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

onMounted(() => {
  // 可以在这里加载真实的房间可用性数据
})
</script>

<style scoped>
.room-calendar {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.calendar-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
}

.calendar-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.cal-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #e5e5e5;
  background: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.cal-btn:hover {
  background: #f5f5f5;
  border-color: #d4d4d4;
}

.today-btn {
  width: auto;
  padding: 0 1rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.month-display {
  font-weight: 600;
  color: #1a1a1a;
  min-width: 100px;
  text-align: center;
}

.calendar-legend {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #666;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-color.available {
  background: #e8f5e9;
  border: 1px solid #4caf50;
}

.legend-color.partial {
  background: #fff3e0;
  border: 1px solid #ff9800;
}

.legend-color.booked {
  background: #ffebee;
  border: 1px solid #f44336;
}

.legend-color.selected {
  background: #e3f2fd;
  border: 1px solid #2196f3;
}

.calendar-grid {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  overflow: hidden;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f8f9fa;
  border-bottom: 1px solid #e5e5e5;
}

.weekday {
  padding: 0.75rem;
  text-align: center;
  font-weight: 500;
  font-size: 0.875rem;
  color: #666;
}

.days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.day {
  aspect-ratio: 1;
  border-right: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  padding: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: white;
}

.day:nth-child(7n) {
  border-right: none;
}

.day.other-month {
  background: #fafafa;
  color: #ccc;
}

.day.past {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.day.today {
  background: #fffbf0;
}

.day.today .day-number {
  font-weight: 600;
  color: #ff9800;
}

.day.available:not(.past):not(.other-month):hover {
  background: #f0f9ff;
}

.day.partial {
  background: #fff8e1;
}

.day.booked {
  background: #ffebee;
  cursor: not-allowed;
}

.day.selected {
  background: #2196f3 !important;
  color: white;
}

.day.selected .day-number,
.day.selected .day-price,
.day.selected .day-rooms {
  color: white;
}

.day.in-range {
  background: #e3f2fd;
}

.day-number {
  font-size: 0.9375rem;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.day-price {
  font-size: 0.75rem;
  color: #4caf50;
  font-weight: 600;
}

.day-rooms {
  font-size: 0.625rem;
  color: #666;
  margin-top: 0.125rem;
}

.calendar-footer {
  margin-top: 1.5rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.selection-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.selection-dates {
  display: flex;
  gap: 2rem;
}

.date-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.date-item .label {
  font-size: 0.875rem;
  color: #666;
}

.date-item .date {
  font-weight: 600;
  color: #1a1a1a;
}

.selection-summary {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nights {
  font-size: 0.875rem;
  color: #666;
}

.total {
  font-size: 1.125rem;
  font-weight: 600;
  color: #4caf50;
}

.selection-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.btn-clear,
.btn-confirm {
  padding: 0.625rem 1.25rem;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-clear {
  background: white;
  border: 1px solid #e5e5e5;
  color: #666;
}

.btn-clear:hover {
  background: #f5f5f5;
}

.btn-confirm {
  background: #2196f3;
  color: white;
}

.btn-confirm:hover {
  background: #1976d2;
}

/* 响应式 */
@media (max-width: 768px) {
  .calendar-legend {
    flex-wrap: wrap;
    gap: 0.75rem;
  }

  .legend-item {
    font-size: 0.75rem;
  }

  .day {
    padding: 0.25rem;
  }

  .day-price,
  .day-rooms {
    font-size: 0.625rem;
  }

  .selection-info {
    flex-direction: column;
    gap: 1rem;
  }

  .selection-dates {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>