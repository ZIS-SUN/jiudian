import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'
import { mockUsers, getDefaultLoginUser } from '@/data/mockData'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token'))
  const userInfo = ref(null)
  const isLoggedIn = ref(!!token.value)

  // 真实API登录
  const login = async (credentials) => {
    try {
      console.log('Store: 开始登录', credentials)

      // 调用真实API
      const response = await loginApi(credentials)
      console.log('Store: API响应', response)

      if (response && response.data) {
        const { token: newToken, user } = response.data

        token.value = newToken
        userInfo.value = user
        isLoggedIn.value = true

        // 保存到localStorage
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(user))

        console.log('Store: 真实API登录成功，状态已更新', {
          token: token.value,
          user: userInfo.value,
          isLoggedIn: isLoggedIn.value
        })

        return response
      } else {
        throw new Error('登录响应格式错误')
      }
    } catch (error) {
      console.error('Store: 真实API登录失败', error)

      // 只在开发环境下显示详细错误信息
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message)
      } else if (error.message) {
        throw new Error(error.message)
      } else {
        throw new Error('登录失败，请检查用户名和密码')
      }
    }
  }

  // 登出
  const logout = () => {
    token.value = null
    userInfo.value = null
    isLoggedIn.value = false
    
    // 清除localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await getUserInfo()
      userInfo.value = response.data
      return response
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 初始化用户信息
  const initUserInfo = () => {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('token')

    if (savedUser && savedToken) {
      userInfo.value = JSON.parse(savedUser)
      token.value = savedToken
      isLoggedIn.value = true
    } else {
      // 如果没有保存的信息，确保清空状态
      userInfo.value = null
      token.value = null
      isLoggedIn.value = false
    }
  }

  // 设置token（用于注册后直接登录）
  const setToken = (newToken) => {
    token.value = newToken
    isLoggedIn.value = true
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUser = (user) => {
    userInfo.value = user
    localStorage.setItem('user', JSON.stringify(user))
  }

  // 判断是否为管理员
  const isAdmin = computed(() => {
    if (!userInfo.value) return false
    // 兼容多种角色格式
    const role = userInfo.value.role
    return role === 'admin' || role === '管理员' || role === 'ADMIN' ||
           userInfo.value.username === 'admin'
  })

  // 判断是否为普通用户
  const isUser = computed(() => {
    if (!userInfo.value) return false
    const role = userInfo.value.role
    return role === 'user' || role === 'customer' || role === '用户' ||
           (!isAdmin.value)
  })

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isUser,
    login,
    logout,
    fetchUserInfo,
    initUserInfo,
    setToken,
    setUser
  }
})