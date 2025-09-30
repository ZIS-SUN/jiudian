<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>{{ loginType === 'user' ? '用户登录' : '管理员登录' }}</h2>
        <p>盛世国际酒店 - 尊贵体验</p>
        <div class="login-type-switch">
          <el-button 
            :type="loginType === 'user' ? 'primary' : 'default'" 
            size="small"
            @click="switchLoginType('user')"
          >
            用户登录
          </el-button>
          <el-button 
            :type="loginType === 'admin' ? 'primary' : 'default'" 
            size="small"
            @click="switchLoginType('admin')"
          >
            管理员登录
          </el-button>
        </div>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <el-button type="text" @click="$router.push('/')"> 返回首页 </el-button>
        <span style="margin: 0 10px">|</span>
        <el-button type="text" @click="$router.push('/register')" v-if="loginType === 'user'">
          立即注册
        </el-button>
        <span v-if="loginType === 'admin'" style="color: #999; font-size: 12px;">
          管理员请联系系统管理员获取账号
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { login } from "@/api/auth";

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref();
const loading = ref(false);
const loginType = ref('user'); // 默认用户登录

const loginForm = reactive({
  username: "",
  password: "",
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  try {
    await loginFormRef.value.validate();
    loading.value = true;

    // 直接使用store登录，store内部会调用真实API
    const response = await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
    });

    console.log('Login.vue: 登录响应', response);

    ElMessage.success("登录成功！");

    // 根据用户角色跳转到不同页面
    if (userStore.isAdmin) {
      router.push("/admin");
    } else {
      router.push("/"); // 普通用户跳转到首页
    }
  } catch (error) {
    console.error("登录失败:", error);
    ElMessage.error(
      error.message || error.response?.data?.message || "登录失败，请检查用户名和密码"
    );
  } finally {
    loading.value = false;
  }
};

// 切换登录类型
const switchLoginType = (type) => {
  loginType.value = type;
  // 清空表单
  loginForm.username = '';
  loginForm.password = '';
  
  // 如果是管理员登录，预填充默认值方便测试
  if (type === 'admin') {
    loginForm.username = 'admin';
    loginForm.password = 'admin123';
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--color-bg-secondary);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 80%, rgba(0, 122, 255, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(52, 199, 89, 0.06) 0%, transparent 50%),
              radial-gradient(circle at 40% 40%, rgba(255, 149, 0, 0.04) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: rotate(0deg) translate(0, 0); }
  33% { transform: rotate(1deg) translate(20px, -20px); }
  66% { transform: rotate(-1deg) translate(-20px, 20px); }
}

.login-box {
  width: 420px;
  padding: 3rem;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--color-border);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.login-header h2 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.login-header p {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
  margin-bottom: 0;
}

.login-type-switch {
  margin-top: 1.25rem;
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.25rem;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.login-type-switch .el-button {
  border-radius: var(--radius-md) !important;
  padding: 0.5rem 1.25rem !important;
  font-size: 0.875rem !important;
  font-weight: 500 !important;
  border: none !important;
  background: transparent !important;
  color: var(--color-text-secondary) !important;
  transition: all var(--transition-fast) !important;
}

.login-type-switch .el-button--primary {
  background: var(--color-bg-primary) !important;
  color: var(--color-primary) !important;
  box-shadow: var(--shadow-sm) !important;
}

.login-type-switch .el-button:hover {
  color: var(--color-text-primary) !important;
}

.login-form {
  margin-bottom: 1.5rem;
}

/* 输入框样式优化 */
.login-form :deep(.el-input__wrapper) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 0.875rem 1rem;
  transition: all var(--transition-fast);
}

.login-form :deep(.el-input__wrapper:hover) {
  background: var(--color-bg-primary);
  border-color: var(--color-separator);
}

.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  background: var(--color-bg-primary);
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.login-form :deep(.el-input__inner) {
  font-size: 0.9375rem;
  color: var(--color-text-primary);
}

.login-form :deep(.el-input__prefix) {
  color: var(--color-text-tertiary);
}

/* 登录按钮样式 */
.login-button {
  width: 100%;
  height: 48px !important;
  background: var(--color-primary) !important;
  border: none !important;
  border-radius: var(--radius-md) !important;
  font-size: 1rem !important;
  font-weight: 600 !important;
  letter-spacing: -0.01em !important;
  transition: all var(--transition-fast) !important;
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.15) !important;
}

.login-button:hover {
  background: var(--color-primary-hover) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.2) !important;
}

.login-button:active {
  transform: translateY(0) !important;
}

.login-footer {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid var(--color-border);
}

.login-footer :deep(.el-button--text) {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.login-footer :deep(.el-button--text:hover) {
  color: var(--color-primary);
}

.login-footer span {
  color: var(--color-text-tertiary);
  font-size: 0.875rem;
}

/* 表单验证错误样式 */
.login-form :deep(.el-form-item__error) {
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: calc(100% - 2rem);
    margin: 1rem;
    padding: 2rem;
  }
  
  .login-header h2 {
    font-size: 1.5rem;
  }
  
  .login-type-switch {
    flex-direction: column;
  }
  
  .login-type-switch .el-button {
    width: 100%;
  }
}
</style>
