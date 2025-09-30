// 统一的预订管理工具
import { mockBookings } from '@/data/mockData'

// 获取所有预订记录（酒店+景点）
export const getAllBookings = () => {
  // 获取酒店预订
  const hotelBookings = JSON.parse(localStorage.getItem('userBookings') || '[]')
  const mockHotelBookings = mockBookings

  // 获取景点预订
  const attractionBookings = JSON.parse(localStorage.getItem('attractionBookings') || '[]')

  // 合并所有预订
  const allHotelBookings = [...hotelBookings, ...mockHotelBookings]

  // 转换格式以统一数据结构
  const formattedHotelBookings = allHotelBookings.map(booking => ({
    id: booking.id || booking.bookingNumber,
    orderId: booking.bookingNumber,
    type: 'hotel',
    customer: {
      id: booking.userId,
      name: booking.userName,
      phone: booking.userPhone,
      email: booking.userEmail
    },
    room: {
      id: booking.roomTypeId,
      roomNumber: booking.roomNumber || '待分配',
      roomType: {
        id: booking.roomTypeId,
        typeName: booking.roomTypeName
      }
    },
    checkInDate: booking.checkInDate,
    checkOutDate: booking.checkOutDate,
    guestCount: booking.guestCount,
    totalPrice: booking.totalAmount,
    status: getUnifiedStatus(booking.status),
    paymentStatus: booking.paymentStatus || 'paid',
    paymentMethod: booking.paymentMethod,
    createdTime: booking.bookingTime,
    notes: booking.specialRequests || '',
    transactionId: booking.transactionId || ''
  }))

  const formattedAttractionBookings = attractionBookings.map(booking => ({
    id: booking.id,
    orderId: booking.orderNumber,
    type: 'attraction',
    customer: {
      id: booking.userId || 'guest',
      name: booking.contactName,
      phone: booking.contactPhone,
      email: ''
    },
    attraction: {
      id: booking.attractionId,
      name: booking.attractionName,
      visitDate: booking.visitDate,
      timeSlot: booking.timeSlot,
      tickets: booking.tickets
    },
    checkInDate: booking.visitDate,
    checkOutDate: booking.visitDate,
    guestCount: (booking.tickets.adult || 0) + (booking.tickets.child || 0) + (booking.tickets.student || 0),
    totalPrice: booking.totalAmount,
    status: booking.status || 'confirmed',
    paymentStatus: 'paid',
    paymentMethod: booking.paymentMethod,
    createdTime: booking.bookingTime,
    notes: booking.notes || '',
    transactionId: booking.transactionId || ''
  }))

  return [...formattedHotelBookings, ...formattedAttractionBookings]
    .sort((a, b) => new Date(b.createdTime) - new Date(a.createdTime))
}

// 获取用户的预订记录
export const getUserBookings = (userId) => {
  const allBookings = getAllBookings()
  return allBookings.filter(booking => booking.customer.id == userId)
}

// 获取按类型分组的预订统计
export const getBookingStatistics = () => {
  const allBookings = getAllBookings()

  const stats = {
    total: allBookings.length,
    hotel: allBookings.filter(b => b.type === 'hotel').length,
    attraction: allBookings.filter(b => b.type === 'attraction').length,
    pending: allBookings.filter(b => b.status === '待确认').length,
    confirmed: allBookings.filter(b => b.status === '已确认').length,
    checkedIn: allBookings.filter(b => b.status === '已入住').length,
    completed: allBookings.filter(b => b.status === '已退房' || b.status === '已完成').length,
    cancelled: allBookings.filter(b => b.status === '已取消').length,
    totalRevenue: allBookings.reduce((sum, booking) => sum + booking.totalPrice, 0)
  }

  return stats
}

// 统一状态映射
const getUnifiedStatus = (status) => {
  const statusMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'checkedin': '已入住',
    'completed': '已退房',
    'cancelled': '已取消'
  }

  if (typeof status === 'string') {
    return statusMap[status.toLowerCase()] || status
  }

  return '未知状态'
}

// 更新预订状态
export const updateBookingStatus = (bookingId, newStatus) => {
  try {
    // 更新酒店预订状态
    const hotelBookings = JSON.parse(localStorage.getItem('userBookings') || '[]')
    const hotelBookingIndex = hotelBookings.findIndex(b => b.id === bookingId || b.bookingNumber === bookingId)

    if (hotelBookingIndex !== -1) {
      hotelBookings[hotelBookingIndex].status = newStatus
      localStorage.setItem('userBookings', JSON.stringify(hotelBookings))
      return true
    }

    // 更新景点预订状态
    const attractionBookings = JSON.parse(localStorage.getItem('attractionBookings') || '[]')
    const attractionBookingIndex = attractionBookings.findIndex(b => b.id === bookingId || b.orderNumber === bookingId)

    if (attractionBookingIndex !== -1) {
      attractionBookings[attractionBookingIndex].status = newStatus
      localStorage.setItem('attractionBookings', JSON.stringify(attractionBookings))
      return true
    }

    // 如果是mockBookings中的数据，直接更新内存中的数据（注意：页面刷新后会重置）
    const mockBookingIndex = mockBookings.findIndex(b => b.id === bookingId || b.bookingNumber === bookingId)
    if (mockBookingIndex !== -1) {
      mockBookings[mockBookingIndex].status = newStatus
      return true
    }

    return false
  } catch (error) {
    console.error('更新预订状态失败:', error)
    return false
  }
}

// 删除/取消预订
export const cancelBooking = (bookingId) => {
  return updateBookingStatus(bookingId, 'cancelled')
}

// 搜索预订
export const searchBookings = (searchParams) => {
  const allBookings = getAllBookings()

  return allBookings.filter(booking => {
    // 按订单号搜索
    if (searchParams.orderId && !booking.orderId.toLowerCase().includes(searchParams.orderId.toLowerCase())) {
      return false
    }

    // 按客户姓名搜索
    if (searchParams.customerName && !booking.customer.name.toLowerCase().includes(searchParams.customerName.toLowerCase())) {
      return false
    }

    // 按状态搜索
    if (searchParams.status && booking.status !== searchParams.status) {
      return false
    }

    // 按类型搜索
    if (searchParams.type && booking.type !== searchParams.type) {
      return false
    }

    // 按日期范围搜索
    if (searchParams.startDate && new Date(booking.checkInDate) < new Date(searchParams.startDate)) {
      return false
    }

    if (searchParams.endDate && new Date(booking.checkInDate) > new Date(searchParams.endDate)) {
      return false
    }

    return true
  })
}

// 导出预订数据
export const exportBookings = (bookings = null) => {
  const data = bookings || getAllBookings()

  const csvContent = [
    // CSV标题行
    '订单号,类型,客户姓名,联系电话,预订日期,状态,总金额,支付方式,创建时间',
    // 数据行
    ...data.map(booking => [
      booking.orderId,
      booking.type === 'hotel' ? '酒店' : '景点',
      booking.customer.name,
      booking.customer.phone,
      booking.checkInDate,
      booking.status,
      booking.totalPrice,
      booking.paymentMethod || '',
      booking.createdTime
    ].join(','))
  ].join('\n')

  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')

  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `bookings_${new Date().toISOString().slice(0, 10)}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

export default {
  getAllBookings,
  getUserBookings,
  getBookingStatistics,
  updateBookingStatus,
  cancelBooking,
  searchBookings,
  exportBookings
}