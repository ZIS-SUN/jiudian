import api from './index'

// 获取订单列表
export const getOrders = (params) => {
  return api.get('/orders', { params })
}

// 获取订单统计数据
export const getOrderStatistics = () => {
  return api.get('/orders/statistics')
}

// 获取订单详情
export const getOrderById = (id) => {
  return api.get(`/orders/${id}`)
}

// 批量更新订单状态
export const batchUpdateOrderStatus = (data) => {
  return api.put('/orders/batch/status', data)
}

// 处理退款
export const processRefund = (id, data) => {
  return api.post(`/orders/${id}/refund`, data)
}

// 导出订单数据
export const exportOrders = (params) => {
  return api.get('/orders/export', { 
    params,
    responseType: 'blob'
  })
}

// 获取订单趋势数据
export const getOrderTrends = (params) => {
  return api.get('/orders/trends', { params })
}

// 获取收入统计
export const getRevenueStatistics = (params) => {
  return api.get('/orders/revenue', { params })
}
