<template>
  <div class="chatbot-container">
    <!-- 聊天按钮 -->
    <transition name="fade">
      <button v-if="!isOpen" @click="toggleChat" class="chat-trigger">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2Z" stroke="white" stroke-width="2" fill="none"/>
          <path d="M7 9H17M7 13H13" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span class="chat-badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
      </button>
    </transition>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chat-window">
        <div class="chat-header">
          <div class="header-left">
            <div class="bot-avatar">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="white" stroke-width="2" fill="none"/>
                <circle cx="9" cy="10" r="1" fill="white"/>
                <circle cx="15" cy="10" r="1" fill="white"/>
                <path d="M8 15C8 15 10 17 12 17C14 17 16 15 16 15" stroke="white" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="header-info">
              <h3>智能客服助手</h3>
              <span class="status">
                <span class="status-dot"></span>
                在线
              </span>
            </div>
          </div>
          <button @click="toggleChat" class="close-btn">
            <svg width="20" height="20" viewBox="0 0 20 20">
              <path d="M15 5L5 15M5 5L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-for="message in messages" :key="message.id"
               :class="['message', message.type]">
            <div class="message-avatar" v-if="message.type === 'bot'">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="10" r="8" fill="#4A90E2"/>
                <circle cx="8" cy="9" r="1" fill="white"/>
                <circle cx="12" cy="9" r="1" fill="white"/>
                <path d="M7 13C7 13 8.5 14 10 14C11.5 14 13 13 13 13" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="message-content">
              <div class="message-bubble" v-html="message.content"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>

          <div v-if="isTyping" class="message bot typing">
            <div class="message-avatar">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="10" r="8" fill="#4A90E2"/>
                <circle cx="8" cy="9" r="1" fill="white"/>
                <circle cx="12" cy="9" r="1" fill="white"/>
                <path d="M7 13C7 13 8.5 14 10 14C11.5 14 13 13 13 13" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷回复 -->
        <div class="quick-replies" v-if="quickReplies.length > 0">
          <button
            v-for="reply in quickReplies"
            :key="reply"
            @click="sendQuickReply(reply)"
            class="quick-reply-btn"
          >
            {{ reply }}
          </button>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <input
            v-model="inputMessage"
            @keypress.enter="sendMessage"
            placeholder="输入您的问题..."
            class="message-input"
          />
          <button @click="sendMessage" class="send-btn" :disabled="!inputMessage.trim()">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M2 10L18 2L14 18L10 10L2 10Z" stroke="currentColor" stroke-width="2" fill="none" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'

const isOpen = ref(false)
const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const unreadCount = ref(0)
const messagesContainer = ref(null)

const quickReplies = ref([
  '房间预订',
  '查看价格',
  '酒店设施',
  '联系前台'
])

// AI回复库
const aiResponses = {
  greeting: [
    '您好！我是星悦酒店的智能客服助手，很高兴为您服务！请问有什么可以帮助您的吗？',
    '欢迎来到星悦酒店！我可以帮您了解房型、价格、预订等信息，请问需要什么帮助？'
  ],
  room: [
    '我们有多种房型可供选择：<br/>• 标准间：¥388/晚<br/>• 豪华间：¥588/晚<br/>• 套房：¥888/晚<br/>• 总统套房：¥1888/晚<br/><br/>您对哪种房型感兴趣呢？',
    '我们提供舒适的客房，所有房间都配备：<br/>• 免费WiFi<br/>• 独立卫浴<br/>• 空调系统<br/>• 迷你吧<br/>• 保险箱<br/><br/>需要我为您推荐合适的房型吗？'
  ],
  price: [
    '当前房价优惠信息：<br/>• 连住3晚享9折优惠<br/>• 提前7天预订享8.5折<br/>• 会员专享8折起<br/><br/>具体价格根据入住日期和房型而定，请问您计划什么时候入住？',
    '我们的房价根据季节和预订情况会有所浮动。建议您提前预订以获得最优价格。需要我为您查询具体日期的价格吗？'
  ],
  facility: [
    '酒店设施包括：<br/>• 24小时前台服务<br/>• 免费停车场<br/>• 健身房和游泳池<br/>• 商务中心<br/>• 餐厅和酒吧<br/>• 会议室<br/>• 洗衣服务<br/><br/>还有什么其他设施您想了解的吗？',
    '我们为客人提供全方位的服务设施，确保您的住宿舒适便利。所有设施对住店客人免费开放。'
  ],
  booking: [
    '预订房间很简单：<br/>1. 选择入住和退房日期<br/>2. 选择房型和房间数量<br/>3. 填写客人信息<br/>4. 确认支付<br/><br/>您可以点击"立即预订"按钮开始预订，或者我可以帮您转接人工客服。',
    '现在预订可享受优惠！您可以通过网站在线预订，也可以拨打我们的预订热线：010-8888-8888'
  ],
  contact: [
    '联系方式：<br/>• 电话：010-8888-8888<br/>• 邮箱：service@starhotel.com<br/>• 地址：北京市朝阳区建国路88号<br/>• 微信：StarHotel<br/><br/>需要我为您转接人工客服吗？',
    '我们的前台24小时为您服务，您也可以直接拨打客服热线或通过在线表单联系我们。'
  ],
  default: [
    '抱歉，我不太理解您的问题。您可以试试：<br/>• 询问房型和价格<br/>• 了解酒店设施<br/>• 预订相关问题<br/>• 联系方式<br/><br/>或者我可以为您转接人工客服。',
    '我可以帮您解答关于酒店房型、价格、设施、预订等问题。请问具体想了解什么呢？'
  ]
}

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && messages.value.length === 0) {
    // 首次打开，发送欢迎消息
    setTimeout(() => {
      addBotMessage(getRandomResponse('greeting'))
    }, 500)
  }
  if (isOpen.value) {
    unreadCount.value = 0
  }
}

const sendMessage = () => {
  const text = inputMessage.value.trim()
  if (!text) return

  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    type: 'user',
    content: text,
    timestamp: new Date()
  })

  inputMessage.value = ''
  scrollToBottom()

  // 显示打字状态
  isTyping.value = true

  // 模拟AI回复
  setTimeout(() => {
    isTyping.value = false
    const response = generateResponse(text)
    addBotMessage(response)
  }, 1000 + Math.random() * 1000)
}

const sendQuickReply = (reply) => {
  inputMessage.value = reply
  sendMessage()
}

const addBotMessage = (content) => {
  messages.value.push({
    id: Date.now(),
    type: 'bot',
    content: content,
    timestamp: new Date()
  })

  if (!isOpen.value) {
    unreadCount.value++
  }

  scrollToBottom()
  updateQuickReplies(content)
}

const generateResponse = (message) => {
  const lowerMessage = message.toLowerCase()

  if (lowerMessage.includes('房间') || lowerMessage.includes('房型') || lowerMessage.includes('客房')) {
    return getRandomResponse('room')
  } else if (lowerMessage.includes('价格') || lowerMessage.includes('多少钱') || lowerMessage.includes('费用')) {
    return getRandomResponse('price')
  } else if (lowerMessage.includes('设施') || lowerMessage.includes('服务') || lowerMessage.includes('配套')) {
    return getRandomResponse('facility')
  } else if (lowerMessage.includes('预订') || lowerMessage.includes('预定') || lowerMessage.includes('订房')) {
    return getRandomResponse('booking')
  } else if (lowerMessage.includes('联系') || lowerMessage.includes('电话') || lowerMessage.includes('地址')) {
    return getRandomResponse('contact')
  } else if (lowerMessage.includes('你好') || lowerMessage.includes('您好') || lowerMessage.includes('hi')) {
    return getRandomResponse('greeting')
  } else {
    return getRandomResponse('default')
  }
}

const getRandomResponse = (category) => {
  const responses = aiResponses[category] || aiResponses.default
  return responses[Math.floor(Math.random() * responses.length)]
}

const updateQuickReplies = (botMessage) => {
  // 根据机器人回复更新快捷回复选项
  if (botMessage.includes('房型')) {
    quickReplies.value = ['查看价格', '预订房间', '房间设施', '更多房型']
  } else if (botMessage.includes('价格')) {
    quickReplies.value = ['立即预订', '会员优惠', '查看房型', '联系客服']
  } else if (botMessage.includes('预订')) {
    quickReplies.value = ['选择日期', '查看房型', '支付方式', '取消政策']
  } else {
    quickReplies.value = ['房间预订', '查看价格', '酒店设施', '联系前台']
  }
}

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 自动问候
onMounted(() => {
  setTimeout(() => {
    if (!isOpen.value) {
      unreadCount.value = 1
    }
  }, 5000)
})
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 9999;
}

/* 聊天触发按钮 */
.chat-trigger {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border: none;
  box-shadow: 0 4px 16px rgba(74, 144, 226, 0.4);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  position: relative;
}

.chat-trigger:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(74, 144, 226, 0.5);
}

.chat-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff4444;
  color: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

/* 聊天窗口 */
.chat-window {
  position: fixed;
  bottom: 6rem;
  right: 2rem;
  width: 380px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.bot-avatar {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-info h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
}

.status {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  opacity: 0.9;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #4caf50;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  background: #f8f9fa;
}

.message {
  display: flex;
  margin-bottom: 1rem;
  animation: messageIn 0.3s ease;
}

@keyframes messageIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  justify-content: flex-end;
}

.message.bot {
  justify-content: flex-start;
}

.message-avatar {
  width: 32px;
  height: 32px;
  margin-right: 0.5rem;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 0.75rem 1rem;
  border-radius: 12px;
  font-size: 0.875rem;
  line-height: 1.5;
  word-wrap: break-word;
}

.message.bot .message-bubble {
  background: white;
  color: #333;
  border: 1px solid #e5e5e5;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  color: white;
}

.message-time {
  font-size: 0.625rem;
  color: #999;
  margin-top: 0.25rem;
  text-align: right;
}

.message.bot .message-time {
  text-align: left;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e5e5;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

/* 快捷回复 */
.quick-replies {
  padding: 0.75rem;
  background: white;
  border-top: 1px solid #e5e5e5;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.quick-reply-btn {
  padding: 0.375rem 0.75rem;
  background: white;
  border: 1px solid #4A90E2;
  color: #4A90E2;
  border-radius: 16px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-reply-btn:hover {
  background: #4A90E2;
  color: white;
}

/* 输入区域 */
.chat-input {
  padding: 1rem;
  background: white;
  border-top: 1px solid #e5e5e5;
  display: flex;
  gap: 0.75rem;
}

.message-input {
  flex: 1;
  padding: 0.625rem 1rem;
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  font-size: 0.875rem;
  outline: none;
  transition: border-color 0.2s;
}

.message-input:focus {
  border-color: #4A90E2;
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.1);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from, .slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式 */
@media (max-width: 480px) {
  .chat-window {
    width: calc(100vw - 2rem);
    height: calc(100vh - 8rem);
    right: 1rem;
    bottom: 5rem;
  }

  .chatbot-container {
    right: 1rem;
    bottom: 1rem;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}
</style>