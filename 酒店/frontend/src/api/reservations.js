import api from './index'

// 获取预订列表
export const getReservations = (params) => {
  return api.get('/reservations', { params })
}

// 获取预订详情
export const getReservationById = (id) => {
  return api.get(`/reservations/${id}`)
}

// 创建预订
export const createReservation = (data) => {
  return api.post('/reservations', data)
}

// 更新预订信息
export const updateReservation = (id, data) => {
  return api.put(`/reservations/${id}`, data)
}

// 取消预订
export const cancelReservation = (id) => {
  return api.put(`/reservations/${id}/cancel`)
}

// 确认预订
export const confirmReservation = (id) => {
  return api.put(`/reservations/${id}/confirm`)
}

// 办理入住
export const checkIn = (id) => {
  return api.put(`/reservations/${id}/checkin`)
}

// 办理退房
export const checkOut = (id) => {
  return api.put(`/reservations/${id}/checkout`)
}