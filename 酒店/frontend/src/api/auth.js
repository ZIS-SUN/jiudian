import api from './index'

// 用户登录
export const login = (data) => {
  return api.post('/auth/login', data)
}

// 用户注册
export const register = (data) => {
  return api.post('/auth/register', data)
}

// 用户登出
export const logout = () => {
  return api.post('/auth/logout')
}

// 获取用户信息
export const getUserInfo = () => {
  return api.get('/auth/user')
}

// 刷新token
export const refreshToken = () => {
  return api.post('/auth/refresh')
}