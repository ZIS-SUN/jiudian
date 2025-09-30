import api from './index'

// 获取客户列表
export const getCustomers = (params) => {
  return api.get('/customers', { params })
}

// 获取客户详情
export const getCustomerById = (id) => {
  return api.get(`/customers/${id}`)
}

// 创建客户
export const createCustomer = (data) => {
  return api.post('/customers', data)
}

// 更新客户信息
export const updateCustomer = (id, data) => {
  return api.put(`/customers/${id}`, data)
}

// 删除客户
export const deleteCustomer = (id) => {
  return api.delete(`/customers/${id}`)
}

// 获取客户的预订历史
export const getCustomerReservations = (id, params) => {
  return api.get(`/customers/${id}/reservations`, { params })
}