import api from './index'

// 获取房间列表
export const getRooms = (params) => {
  return api.get('/rooms', { params })
}

// 获取房间详情
export const getRoomById = (id) => {
  return api.get(`/rooms/${id}`)
}

// 创建房间
export const createRoom = (data) => {
  return api.post('/rooms', data)
}

// 更新房间信息
export const updateRoom = (id, data) => {
  return api.put(`/rooms/${id}`, data)
}

// 删除房间
export const deleteRoom = (id) => {
  return api.delete(`/rooms/${id}`)
}

// 获取房间类型
export const getRoomTypes = () => {
  return api.get('/rooms/types')
}