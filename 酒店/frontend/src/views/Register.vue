<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的酒店管理系统账户</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            prefix-icon="UserFilled"
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            type="email"
            clearable
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            type="password"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            placeholder="请确认密码"
            prefix-icon="Lock"
            type="password"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? "注册中..." : "注册" }}
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账户？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { register } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const registerFormRef = ref();

const registerForm = reactive({
  username: "",
  realName: "",
  email: "",
  phone: "",
  password: "",
  confirmPassword: "",
});

const validateConfirmPassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerForm.password) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 4, max: 20, message: "用户名长度为4-20位", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: "用户名只能包含字母、数字和下划线",
      trigger: "blur",
    },
  ],
  realName: [
    { required: true, message: "请输入真实姓名", trigger: "blur" },
    { min: 2, max: 20, message: "真实姓名长度为2-20位", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
  ],
  phone: [
    {
      pattern: /^1[3456789]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度为6-20位", trigger: "blur" },
    {
      pattern: /^(?=.*[a-zA-Z])(?=.*\d)/,
      message: "密码必须包含字母和数字",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
    loading.value = true;

    // 准备注册数据
    const registerData = {
      username: registerForm.username,
      realName: registerForm.realName,
      email: registerForm.email,
      phone: registerForm.phone,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      role: '员工' // 注册的默认为员工角色
    };

    // 调用真实的注册API
    const response = await register(registerData);

    if (response && response.data) {
      const { token, user } = response.data;

      // 保存token和用户信息
      userStore.setToken(token);
      userStore.setUser(user);

      ElMessage.success("注册成功！");
      router.push("/"); // 注册成功跳转到首页
    } else {
      throw new Error("注册失败，请稍后重试");
    }
  } catch (error) {
    console.error("注册失败:", error);
    ElMessage.error(error.response?.data?.message || error.message || "注册失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--color-bg-secondary);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.register-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 70% 30%, rgba(0, 122, 255, 0.06) 0%, transparent 50%),
              radial-gradient(circle at 20% 80%, rgba(52, 199, 89, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 50% 50%, rgba(255, 149, 0, 0.04) 0%, transparent 50%);
  animation: float 25s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: rotate(0deg) translate(0, 0); }
  33% { transform: rotate(1deg) translate(30px, -30px); }
  66% { transform: rotate(-1deg) translate(-20px, 20px); }
}

.register-box {
  width: 100%;
  max-width: 480px;
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--color-border);
  padding: 3rem;
  position: relative;
  z-index: 1;
}

.register-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.register-header h2 {
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
  font-weight: 700;
  font-size: 1.75rem;
  letter-spacing: -0.02em;
}

.register-header p {
  color: var(--color-text-secondary);
  font-size: 0.9375rem;
  margin: 0;
}

.register-form {
  margin-top: 2rem;
}

.register-form .el-form-item {
  margin-bottom: 1.5rem;
}

/* 输入框样式 */
.register-form :deep(.el-input__wrapper) {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 0.875rem 1rem;
  transition: all var(--transition-fast);
  box-shadow: none;
}

.register-form :deep(.el-input__wrapper:hover) {
  background: var(--color-bg-primary);
  border-color: var(--color-separator);
}

.register-form :deep(.el-input.is-focus .el-input__wrapper) {
  background: var(--color-bg-primary);
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.register-form :deep(.el-input__inner) {
  font-size: 0.9375rem;
  color: var(--color-text-primary);
}

.register-form :deep(.el-input__prefix) {
  color: var(--color-text-tertiary);
}

/* 注册按钮 */
.register-btn {
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

.register-btn:hover {
  background: var(--color-primary-hover) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.2) !important;
}

.register-btn:active {
  transform: translateY(0) !important;
}

.register-footer {
  text-align: center;
  margin-top: 1.5rem;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.login-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  margin-left: 0.25rem;
  transition: color var(--transition-fast);
}

.login-link:hover {
  color: var(--color-primary-hover);
}

/* 表单验证错误样式 */
.register-form :deep(.el-form-item__error) {
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-box {
    width: calc(100% - 2rem);
    margin: 1rem;
    padding: 2rem;
  }
  
  .register-header h2 {
    font-size: 1.5rem;
  }
  
  .register-form .el-form-item {
    margin-bottom: 1.25rem;
  }
}
</style>
