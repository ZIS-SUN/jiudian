import api from './index'

/**
 * 获取公告列表（分页）
 */
export const getAnnouncementList = (params) => {
  return api.get('/announcements', { params })
}

/**
 * 获取所有启用的公告
 */
export const getActiveAnnouncements = () => {
  return api.get('/announcements/active')
}

/**
 * 根据ID获取公告
 */
export const getAnnouncementById = (id) => {
  return api.get(`/announcements/${id}`)
}

/**
 * 创建公告
 */
export const createAnnouncement = (data) => {
  return api.post('/announcements', data)
}

/**
 * 更新公告
 */
export const updateAnnouncement = (id, data) => {
  return api.put(`/announcements/${id}`, data)
}

/**
 * 删除公告
 */
export const deleteAnnouncement = (id) => {
  return api.delete(`/announcements/${id}`)
}