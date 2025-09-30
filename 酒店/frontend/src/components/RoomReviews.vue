<template>
  <div class="room-reviews">
    <div class="reviews-header">
      <h3>客户评价</h3>
      <div class="rating-summary">
        <div class="overall-rating">
          <span class="rating-number">{{ averageRating.toFixed(1) }}</span>
          <div class="rating-stars">
            <span class="stars-filled" :style="{ width: (averageRating / 5 * 100) + '%' }">★★★★★</span>
            <span class="stars-empty">★★★★★</span>
          </div>
          <span class="review-count">{{ reviews.length }} 条评价</span>
        </div>
        <div class="rating-breakdown">
          <div v-for="i in 5" :key="i" class="rating-bar">
            <span class="bar-label">{{ 6 - i }}★</span>
            <div class="bar-container">
              <div class="bar-fill" :style="{ width: getRatingPercentage(6 - i) + '%' }"></div>
            </div>
            <span class="bar-count">{{ getRatingCount(6 - i) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="review-actions">
      <el-button v-if="canWriteReview" type="primary" @click="showWriteReview = true">
        写评价
      </el-button>
      <div class="filter-sort">
        <el-select v-model="sortBy" placeholder="排序方式" size="small">
          <el-option label="最新优先" value="newest" />
          <el-option label="最早优先" value="oldest" />
          <el-option label="评分最高" value="highest" />
          <el-option label="评分最低" value="lowest" />
        </el-select>
      </div>
    </div>

    <div class="reviews-list">
      <div v-for="review in sortedReviews" :key="review.id" class="review-item">
        <div class="review-header">
          <div class="reviewer-info">
            <div class="reviewer-avatar">{{ review.userName.charAt(0) }}</div>
            <div class="reviewer-details">
              <span class="reviewer-name">{{ review.userName }}</span>
              <span class="review-date">{{ formatDate(review.date) }}</span>
            </div>
          </div>
          <div class="review-rating">
            <span class="stars">{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</span>
          </div>
        </div>
        <div class="review-content">
          <p>{{ review.comment }}</p>
          <div v-if="review.images && review.images.length > 0" class="review-images">
            <img v-for="(img, idx) in review.images" :key="idx" :src="img" @click="previewImage(img)" />
          </div>
        </div>
        <div class="review-footer">
          <button class="helpful-btn" @click="toggleHelpful(review.id)">
            <span :class="{ active: review.helpful }">👍</span> 有帮助 ({{ review.helpfulCount || 0 }})
          </button>
          <button v-if="review.reply" class="reply-toggle" @click="review.showReply = !review.showReply">
            查看酒店回复
          </button>
        </div>
        <div v-if="review.reply && review.showReply" class="hotel-reply">
          <div class="reply-header">
            <span class="reply-badge">酒店回复</span>
            <span class="reply-date">{{ formatDate(review.replyDate) }}</span>
          </div>
          <p>{{ review.reply }}</p>
        </div>
      </div>

      <div v-if="sortedReviews.length === 0" class="no-reviews">
        <p>暂无评价，成为第一个评价者吧！</p>
      </div>
    </div>

    <!-- 写评价弹窗 -->
    <el-dialog
      v-model="showWriteReview"
      title="写评价"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="newReview" :rules="reviewRules" ref="reviewFormRef">
        <el-form-item label="评分" prop="rating">
          <div class="rating-input">
            <span
              v-for="i in 5"
              :key="i"
              @click="newReview.rating = i"
              @mouseenter="hoverRating = i"
              @mouseleave="hoverRating = 0"
              class="star-btn"
              :class="{ filled: i <= (hoverRating || newReview.rating) }"
            >
              ★
            </span>
            <span class="rating-text">{{ getRatingText(newReview.rating) }}</span>
          </div>
        </el-form-item>
        <el-form-item label="评价内容" prop="comment">
          <el-input
            v-model="newReview.comment"
            type="textarea"
            :rows="5"
            placeholder="分享您的入住体验..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <div class="image-upload">
            <div v-for="(img, idx) in newReview.images" :key="idx" class="image-preview">
              <img :src="img" />
              <button @click="removeImage(idx)" class="remove-btn">×</button>
            </div>
            <button v-if="newReview.images.length < 5" class="add-image-btn" @click="addImage">
              + 添加图片
            </button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWriteReview = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  roomTypeId: {
    type: [String, Number],
    required: true
  }
})

const userStore = useUserStore()
const showWriteReview = ref(false)
const submitting = ref(false)
const sortBy = ref('newest')
const hoverRating = ref(0)
const reviewFormRef = ref()

const reviews = ref([])

const newReview = reactive({
  rating: 5,
  comment: '',
  images: []
})

const reviewRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' },
    { type: 'number', min: 1, max: 5, message: '评分必须在1-5之间', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0)
  return sum / reviews.value.length
})

const sortedReviews = computed(() => {
  const sorted = [...reviews.value]
  switch (sortBy.value) {
    case 'newest':
      return sorted.sort((a, b) => new Date(b.date) - new Date(a.date))
    case 'oldest':
      return sorted.sort((a, b) => new Date(a.date) - new Date(b.date))
    case 'highest':
      return sorted.sort((a, b) => b.rating - a.rating)
    case 'lowest':
      return sorted.sort((a, b) => a.rating - b.rating)
    default:
      return sorted
  }
})

const canWriteReview = computed(() => {
  // 检查用户是否登录且有过该房型的入住记录
  return userStore.isLoggedIn
})

const getRatingPercentage = (rating) => {
  const count = reviews.value.filter(r => r.rating === rating).length
  return reviews.value.length > 0 ? (count / reviews.value.length * 100) : 0
}

const getRatingCount = (rating) => {
  return reviews.value.filter(r => r.rating === rating).length
}

const getRatingText = (rating) => {
  const texts = ['', '非常差', '差', '一般', '好', '非常好']
  return texts[rating] || ''
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const toggleHelpful = (reviewId) => {
  const review = reviews.value.find(r => r.id === reviewId)
  if (review) {
    review.helpful = !review.helpful
    review.helpfulCount = (review.helpfulCount || 0) + (review.helpful ? 1 : -1)
    // 保存到localStorage
    saveReviews()
  }
}

const previewImage = (img) => {
  // 实现图片预览功能
  window.open(img, '_blank')
}

const addImage = () => {
  // 模拟添加图片
  const demoImages = [
    'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=200&h=150&fit=crop',
    'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=200&h=150&fit=crop'
  ]
  if (newReview.images.length < 5) {
    newReview.images.push(demoImages[newReview.images.length % 2])
  }
}

const removeImage = (index) => {
  newReview.images.splice(index, 1)
}

const submitReview = async () => {
  try {
    await reviewFormRef.value.validate()
    submitting.value = true

    const review = {
      id: Date.now(),
      roomTypeId: props.roomTypeId,
      userName: userStore.userInfo?.name || userStore.userInfo?.username || '匿名用户',
      userId: userStore.userInfo?.id,
      rating: newReview.rating,
      comment: newReview.comment,
      images: [...newReview.images],
      date: new Date().toISOString(),
      helpful: false,
      helpfulCount: 0,
      showReply: false
    }

    reviews.value.unshift(review)
    saveReviews()

    ElMessage.success('评价提交成功！')
    showWriteReview.value = false
    resetForm()
  } catch (error) {
    console.error('提交评价失败:', error)
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  newReview.rating = 5
  newReview.comment = ''
  newReview.images = []
  hoverRating.value = 0
}

const loadReviews = () => {
  // 从localStorage加载评价数据
  const storedReviews = JSON.parse(localStorage.getItem('roomReviews') || '[]')
  const roomReviews = storedReviews.filter(r => r.roomTypeId == props.roomTypeId)

  // 如果没有评价，添加一些示例数据
  if (roomReviews.length === 0) {
    roomReviews.push(
      {
        id: 1,
        roomTypeId: props.roomTypeId,
        userName: '张先生',
        rating: 5,
        comment: '房间很干净，服务态度也很好，位置方便，下次还会选择这里。',
        date: new Date(Date.now() - 86400000 * 3).toISOString(),
        helpfulCount: 12,
        reply: '感谢您的好评！期待您的再次光临。',
        replyDate: new Date(Date.now() - 86400000 * 2).toISOString()
      },
      {
        id: 2,
        roomTypeId: props.roomTypeId,
        userName: '李女士',
        rating: 4,
        comment: '整体不错，就是隔音效果一般，早上被走廊的声音吵醒了。',
        date: new Date(Date.now() - 86400000 * 7).toISOString(),
        helpfulCount: 8,
        images: ['https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=200&h=150&fit=crop']
      },
      {
        id: 3,
        roomTypeId: props.roomTypeId,
        userName: '王先生',
        rating: 5,
        comment: '性价比很高，早餐丰富，工作人员很友好。',
        date: new Date(Date.now() - 86400000 * 10).toISOString(),
        helpfulCount: 5
      }
    )
  }

  reviews.value = roomReviews
}

const saveReviews = () => {
  const allReviews = JSON.parse(localStorage.getItem('roomReviews') || '[]')
  const otherReviews = allReviews.filter(r => r.roomTypeId != props.roomTypeId)
  const updatedReviews = [...otherReviews, ...reviews.value]
  localStorage.setItem('roomReviews', JSON.stringify(updatedReviews))
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.room-reviews {
  padding: 2rem 0;
}

.reviews-header h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
}

.rating-summary {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 3rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.overall-rating {
  text-align: center;
}

.rating-number {
  font-size: 3rem;
  font-weight: 600;
  color: var(--color-primary);
  display: block;
}

.rating-stars {
  position: relative;
  font-size: 1.25rem;
  margin: 0.5rem 0;
}

.stars-filled {
  position: absolute;
  color: #ffa500;
  overflow: hidden;
  white-space: nowrap;
}

.stars-empty {
  color: #ddd;
}

.review-count {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.rating-breakdown {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.bar-label {
  width: 2rem;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.bar-container {
  flex: 1;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: #ffa500;
  transition: width 0.3s;
}

.bar-count {
  width: 2rem;
  text-align: right;
  font-size: 0.875rem;
  color: var(--color-text-tertiary);
}

.review-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.filter-sort {
  display: flex;
  gap: 1rem;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.review-item {
  padding: 1.5rem;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.reviewer-info {
  display: flex;
  gap: 0.75rem;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.reviewer-details {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.reviewer-name {
  font-weight: 500;
  color: var(--color-text-primary);
}

.review-date {
  font-size: 0.875rem;
  color: var(--color-text-tertiary);
}

.review-rating .stars {
  color: #ffa500;
  font-size: 1rem;
}

.review-content {
  margin-bottom: 1rem;
}

.review-content p {
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: 1rem;
}

.review-images {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.review-images img {
  width: 100px;
  height: 75px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: transform 0.2s;
}

.review-images img:hover {
  transform: scale(1.05);
}

.review-footer {
  display: flex;
  gap: 1rem;
}

.helpful-btn,
.reply-toggle {
  background: none;
  border: 1px solid var(--color-border);
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.helpful-btn:hover,
.reply-toggle:hover {
  background: var(--color-bg-secondary);
}

.helpful-btn .active {
  color: var(--color-primary);
}

.hotel-reply {
  margin-top: 1rem;
  padding: 1rem;
  background: var(--color-bg-tertiary);
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--color-primary);
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.reply-badge {
  background: var(--color-primary);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  font-weight: 500;
}

.reply-date {
  font-size: 0.875rem;
  color: var(--color-text-tertiary);
}

.hotel-reply p {
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.no-reviews {
  text-align: center;
  padding: 3rem;
  color: var(--color-text-tertiary);
}

/* 评分输入 */
.rating-input {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.star-btn {
  font-size: 2rem;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star-btn.filled {
  color: #ffa500;
}

.rating-text {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

/* 图片上传 */
.image-upload {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.image-preview {
  position: relative;
  width: 100px;
  height: 75px;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.remove-btn {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--color-danger);
  color: white;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-image-btn {
  width: 100px;
  height: 75px;
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-bg-secondary);
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: all 0.2s;
}

.add-image-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* Element Plus 样式覆盖 */
:deep(.el-dialog__header) {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-dialog__body) {
  padding: 1.5rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
}

:deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
  resize: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .rating-summary {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .review-actions {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .review-header {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>