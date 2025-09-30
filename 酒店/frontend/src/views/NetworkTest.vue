<template>
  <div class="network-test">
    <h2>网络连接测试</h2>

    <div class="test-section">
      <h3>后端服务状态</h3>
      <el-button @click="testConnection" :loading="loading">测试连接</el-button>
      <p v-if="connectionStatus" :class="connectionStatus.success ? 'success' : 'error'">
        {{ connectionStatus.message }}
      </p>
    </div>

    <div class="test-section">
      <h3>登录测试</h3>
      <el-form :model="loginForm" @submit.prevent="testLogin">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="输入测试用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="输入测试密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testLogin" :loading="loginLoading">测试登录</el-button>
        </el-form-item>
      </el-form>
      <p v-if="loginResult" :class="loginResult.success ? 'success' : 'error'">
        {{ loginResult.message }}
      </p>
    </div>

    <div class="test-section">
      <h3>详细配置信息</h3>
      <p><strong>前端URL:</strong> {{ frontendUrl }}</p>
      <p><strong>后端API URL:</strong> {{ backendUrl }}</p>
      <p><strong>当前时间:</strong> {{ currentTime }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import api from '@/api/index'

const loading = ref(false)
const loginLoading = ref(false)
const connectionStatus = ref(null)
const loginResult = ref(null)
const frontendUrl = ref('')
const backendUrl = ref('http://localhost:8089/api')
const currentTime = ref('')

const loginForm = ref({
  username: 'zhangsan',
  password: 'admin123'
})

const updateTime = () => {
  currentTime.value = new Date().toLocaleString()
}

const testConnection = async () => {
  loading.value = true
  connectionStatus.value = null

  try {
    // 直接使用 fetch 测试连接，避免 axios 拦截器的影响
    const response = await fetch(`${backendUrl.value}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: 'test',
        password: 'test'
      })
    })

    if (response.ok || response.status === 500) {
      // 即使登录失败（500错误）也说明连接正常
      connectionStatus.value = {
        success: true,
        message: `✅ 后端连接正常 (HTTP ${response.status})`
      }
    } else {
      connectionStatus.value = {
        success: false,
        message: `❌ 后端响应异常: HTTP ${response.status}`
      }
    }
  } catch (error) {
    console.error('连接测试失败:', error)
    connectionStatus.value = {
      success: false,
      message: `❌ 连接失败: ${error.message}`
    }
  } finally {
    loading.value = false
  }
}

const testLogin = async () => {
  loginLoading.value = true
  loginResult.value = null

  try {
    console.log('开始登录测试:', loginForm.value)
    const result = await login(loginForm.value)
    console.log('登录测试成功:', result)

    loginResult.value = {
      success: true,
      message: `✅ 登录成功! 用户: ${result.data.user.realName}`
    }
  } catch (error) {
    console.error('登录测试失败:', error)
    loginResult.value = {
      success: false,
      message: `❌ 登录失败: ${error.message || '未知错误'}`
    }
  } finally {
    loginLoading.value = false
  }
}

onMounted(() => {
  frontendUrl.value = window.location.origin
  updateTime()
  setInterval(updateTime, 1000)

  // 自动测试连接
  setTimeout(testConnection, 1000)
})
</script>

<style scoped>
.network-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.success {
  color: #67c23a;
  font-weight: bold;
}

.error {
  color: #f56c6c;
  font-weight: bold;
}

h2, h3 {
  margin-bottom: 15px;
}

p {
  margin: 10px 0;
}

.el-form {
  max-width: 400px;
}
</style>