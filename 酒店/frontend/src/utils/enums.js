// 枚举映射工具
// 前端显示值 -> 后端API值的映射

// 房间状态枚举
export const ROOM_STATUS = {
  DISPLAY_TO_API: {
    '可用': 'AVAILABLE',
    '已入住': 'OCCUPIED', 
    '维护中': 'MAINTENANCE',
    '故障': 'OUT_OF_ORDER'
  },
  API_TO_DISPLAY: {
    'AVAILABLE': '可用',
    'OCCUPIED': '已入住',
    'MAINTENANCE': '维护中', 
    'OUT_OF_ORDER': '故障'
  }
}

// 性别枚举
export const GENDER = {
  DISPLAY_TO_API: {
    '男': 'MALE',
    '女': 'FEMALE'
  },
  API_TO_DISPLAY: {
    'MALE': '男',
    'FEMALE': '女'
  }
}

// 预订状态枚举
export const RESERVATION_STATUS = {
  DISPLAY_TO_API: {
    '待确认': 'PENDING',
    '已确认': 'CONFIRMED',
    '已入住': 'CHECKED_IN',
    '已退房': 'CHECKED_OUT',
    '已取消': 'CANCELLED'
  },
  API_TO_DISPLAY: {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'CHECKED_IN': '已入住',
    'CHECKED_OUT': '已退房',
    'CANCELLED': '已取消'
  }
}

// 工具函数：将显示值转换为API值
export function displayToApi(enumMap, displayValue) {
  return enumMap.DISPLAY_TO_API[displayValue] || displayValue
}

// 工具函数：将API值转换为显示值
export function apiToDisplay(enumMap, apiValue) {
  return enumMap.API_TO_DISPLAY[apiValue] || apiValue
}

// 房间状态相关工具
export const roomStatusUtils = {
  toApi: (displayValue) => displayToApi(ROOM_STATUS, displayValue),
  toDisplay: (apiValue) => apiToDisplay(ROOM_STATUS, apiValue),
  getTagType: (displayValue) => {
    const typeMap = {
      '可用': 'success',
      '已入住': 'warning',
      '维护中': 'danger',
      '故障': 'info'
    }
    return typeMap[displayValue] || 'info'
  }
}

// 性别相关工具
export const genderUtils = {
  toApi: (displayValue) => displayToApi(GENDER, displayValue),
  toDisplay: (apiValue) => apiToDisplay(GENDER, apiValue)
}

// 预订状态相关工具
export const reservationStatusUtils = {
  toApi: (displayValue) => displayToApi(RESERVATION_STATUS, displayValue),
  toDisplay: (apiValue) => apiToDisplay(RESERVATION_STATUS, apiValue),
  getTagType: (displayValue) => {
    const typeMap = {
      '待确认': 'warning',
      '已确认': 'success',
      '已入住': 'primary',
      '已退房': 'info',
      '已取消': 'danger'
    }
    return typeMap[displayValue] || 'info'
  }
}