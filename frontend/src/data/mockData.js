// 酒店管理系统虚拟数据集

// 用户数据
export const mockUsers = [
  // 管理员账户
  {
    id: 1,
    username: 'admin',
    password: 'admin123',
    name: '张管理',
    email: 'admin@hotel.com',
    phone: '13800138000',
    role: 'admin',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    department: '总经理办公室',
    position: '总经理',
    joinDate: '2020-01-15',
    status: 'active'
  },
  {
    id: 2,
    username: 'manager',
    password: 'manager123',
    name: '李经理',
    email: 'manager@hotel.com',
    phone: '13800138001',
    role: 'admin',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    department: '前台部',
    position: '前台经理',
    joinDate: '2020-03-10',
    status: 'active'
  },
  {
    id: 3,
    username: 'receptionist',
    password: 'rec123',
    name: '王小美',
    email: 'wang@hotel.com',
    phone: '13800138002',
    role: 'staff',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
    department: '前台部',
    position: '前台接待员',
    joinDate: '2021-06-01',
    status: 'active'
  },

  // 普通客户账户
  {
    id: 101,
    username: 'zhangsan',
    password: 'admin123',
    name: '张三',
    email: 'zhangsan@email.com',
    phone: '13912345678',
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'gold',
    totalBookings: 15,
    totalSpent: 28500,
    joinDate: '2022-05-20',
    birthday: '1985-03-15',
    idCard: '110101198503151234',
    preferences: ['海景房', '无烟房间', '高楼层'],
    status: 'active'
  },
  {
    id: 102,
    username: 'lisi',
    password: 'admin123',
    name: '李四',
    email: 'lisi@email.com',
    phone: '13987654321',
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'silver',
    totalBookings: 8,
    totalSpent: 12600,
    joinDate: '2022-08-10',
    birthday: '1990-07-22',
    idCard: '110101199007221234',
    preferences: ['商务房', '安静环境'],
    status: 'active'
  },
  {
    id: 103,
    username: 'wangwu',
    password: 'admin123',
    name: '王五',
    email: 'wangwu@email.com',
    phone: '13765432198',
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'bronze',
    totalBookings: 3,
    totalSpent: 4500,
    joinDate: '2023-02-14',
    birthday: '1988-11-30',
    idCard: '110101198811301234',
    preferences: ['家庭房', '儿童设施'],
    status: 'active'
  },
  {
    id: 104,
    username: 'zhaoliu',
    password: 'admin123',
    name: '赵六',
    email: 'zhaoliu@email.com',
    phone: '13654321987',
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'platinum',
    totalBookings: 25,
    totalSpent: 65000,
    joinDate: '2021-12-01',
    birthday: '1982-09-18',
    idCard: '110101198209181234',
    preferences: ['总统套房', '豪华服务', '管家服务'],
    status: 'active'
  },
  {
    id: 105,
    username: 'sunqi',
    password: 'admin123',
    name: '孙七',
    email: 'sunqi@email.com',
    phone: '13543219876',
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'gold',
    totalBookings: 12,
    totalSpent: 18900,
    joinDate: '2022-03-25',
    birthday: '1995-12-05',
    idCard: '110101199512051234',
    preferences: ['景观房', '健身房', '游泳池'],
    status: 'active'
  }
];

// 房间类型数据
export const mockRoomTypes = [
  {
    id: 1,
    name: '豪华海景大床房',
    nameEn: 'Deluxe Ocean View King Room',
    price: 688,
    area: 45,
    capacity: 2,
    bedType: '特大床',
    description: '享有迷人海景的豪华客房，配备现代化设施和优雅装饰，为您提供无与伦比的住宿体验。',
    images: [
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '空调', '液晶电视', '迷你吧', '保险箱', '吹风机', '浴缸', '海景阳台', '24小时客房服务', '免费早餐'],
    features: ['海景', '阳台', '特大床', '现代装修'],
    floorRange: '15-25楼',
    totalRooms: 20,
    availableRooms: 8,
    rating: 4.8,
    reviewCount: 156
  },
  {
    id: 2,
    name: '商务精英套房',
    nameEn: 'Business Elite Suite',
    price: 988,
    area: 65,
    capacity: 3,
    bedType: '特大床+沙发床',
    description: '为商务精英设计的宽敞套房，配备独立办公区域和会议设施，完美融合商务与舒适。',
    images: [
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '空调', '大屏电视', '办公桌', '保险箱', '胶囊咖啡机', '独立客厅', '商务中心', '会议设施', '管家服务'],
    features: ['独立客厅', '办公区域', '商务设施', '高档装修'],
    floorRange: '20-30楼',
    totalRooms: 15,
    availableRooms: 5,
    rating: 4.9,
    reviewCount: 89
  },
  {
    id: 3,
    name: '家庭亲子房',
    nameEn: 'Family Room',
    price: 888,
    area: 55,
    capacity: 4,
    bedType: '双床+儿童床',
    description: '专为家庭设计的温馨客房，配备儿童安全设施和娱乐设备，让全家享受美好时光。',
    images: [
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '空调', '儿童电视频道', '儿童床', '安全插座', '儿童浴袍', '游戏区域', '婴儿床', '儿童洗漱用品', '家庭早餐'],
    features: ['家庭友好', '儿童设施', '安全防护', '娱乐设备'],
    floorRange: '10-18楼',
    totalRooms: 25,
    availableRooms: 12,
    rating: 4.7,
    reviewCount: 203
  },
  {
    id: 4,
    name: '标准双床房',
    nameEn: 'Standard Twin Room',
    price: 388,
    area: 32,
    capacity: 2,
    bedType: '双床',
    description: '经济实惠的舒适客房，提供基本设施和优质服务，是商务出行的理想选择。',
    images: [
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '空调', '液晶电视', '迷你冰箱', '保险箱', '吹风机', '淋浴间', '免费洗漱用品', '24小时热水', '客房服务'],
    features: ['经济实惠', '双床配置', '基础设施', '清洁舒适'],
    floorRange: '5-15楼',
    totalRooms: 40,
    availableRooms: 18,
    rating: 4.5,
    reviewCount: 324
  },
  {
    id: 5,
    name: '总统套房',
    nameEn: 'Presidential Suite',
    price: 2888,
    area: 120,
    capacity: 6,
    bedType: '特大床+双床+沙发床',
    description: '奢华的总统级套房，享有城市全景，配备私人管家和顶级设施，尽享尊贵体验。',
    images: [
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '智能家居', '75寸电视', '私人酒吧', '按摩浴缸', '蒸汽房', '私人阳台', '管家服务', '米其林早餐', '豪华车接送'],
    features: ['奢华装修', '全景视野', '私人管家', '顶级设施'],
    floorRange: '30-35楼',
    totalRooms: 5,
    availableRooms: 2,
    rating: 5.0,
    reviewCount: 45
  },
  {
    id: 6,
    name: '景观大床房',
    nameEn: 'Scenic King Room',
    price: 588,
    area: 40,
    capacity: 2,
    bedType: '特大床',
    description: '享有城市景观的雅致客房，现代化设计与舒适完美结合，让您感受都市繁华。',
    images: [
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop',
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop'
    ],
    amenities: ['免费WiFi', '空调', '智能电视', '迷你吧', '保险箱', '雨林花洒', '景观窗', '遮光窗帘', '高档床品', '客房服务'],
    features: ['城市景观', '现代设计', '大窗户', '舒适环境'],
    floorRange: '12-22楼',
    totalRooms: 30,
    availableRooms: 14,
    rating: 4.6,
    reviewCount: 198
  }
];

// 预订记录数据
export const mockBookings = [
  {
    id: 'HT20241201001',
    bookingNumber: 'HT20241201001',
    userId: 101,
    userName: '张三',
    userPhone: '13912345678',
    userEmail: 'zhangsan@email.com',
    roomTypeId: 1,
    roomTypeName: '豪华海景大床房',
    roomNumber: '2015',
    checkInDate: '2024-12-15',
    checkOutDate: '2024-12-18',
    nights: 3,
    roomCount: 1,
    guestCount: 2,
    totalAmount: 2380,
    paidAmount: 2380,
    status: 'confirmed',
    paymentStatus: 'paid',
    paymentMethod: 'WECHAT',
    bookingTime: '2024-12-01 14:30:00',
    specialRequests: '希望安排高楼层房间，无烟房',
    source: 'online',
    rating: 5,
    review: '房间很棒，海景非常美，服务也很周到！',
    checkInTime: null,
    checkOutTime: null
  },
  {
    id: 'HT20241202001',
    bookingNumber: 'HT20241202001',
    userId: 102,
    userName: '李四',
    userPhone: '13987654321',
    userEmail: 'lisi@email.com',
    roomTypeId: 2,
    roomTypeName: '商务精英套房',
    roomNumber: '2508',
    checkInDate: '2024-12-20',
    checkOutDate: '2024-12-22',
    nights: 2,
    roomCount: 1,
    guestCount: 1,
    totalAmount: 2138,
    paidAmount: 500,
    status: 'pending',
    paymentStatus: 'partial',
    paymentMethod: 'ALIPAY',
    bookingTime: '2024-12-02 09:15:00',
    specialRequests: '需要安静的办公环境，提供额外的办公用品',
    source: 'phone',
    rating: null,
    review: null,
    checkInTime: null,
    checkOutTime: null
  },
  {
    id: 'HT20241125001',
    bookingNumber: 'HT20241125001',
    userId: 103,
    userName: '王五',
    userPhone: '13765432198',
    userEmail: 'wangwu@email.com',
    roomTypeId: 3,
    roomTypeName: '家庭亲子房',
    roomNumber: '1205',
    checkInDate: '2024-11-25',
    checkOutDate: '2024-11-28',
    nights: 3,
    roomCount: 1,
    guestCount: 4,
    totalAmount: 2894,
    paidAmount: 2894,
    status: 'completed',
    paymentStatus: 'paid',
    paymentMethod: 'BANK_CARD',
    bookingTime: '2024-11-20 16:45:00',
    specialRequests: '需要儿童床和婴儿洗漱用品',
    source: 'online',
    rating: 4,
    review: '家庭房设施很齐全，孩子们很喜欢，下次还会选择。',
    checkInTime: '2024-11-25 15:20:00',
    checkOutTime: '2024-11-28 11:30:00'
  },
  {
    id: 'HT20241203001',
    bookingNumber: 'HT20241203001',
    userId: 104,
    userName: '赵六',
    userPhone: '13654321987',
    userEmail: 'zhaoliu@email.com',
    roomTypeId: 5,
    roomTypeName: '总统套房',
    roomNumber: '3201',
    checkInDate: '2024-12-10',
    checkOutDate: '2024-12-13',
    nights: 3,
    roomCount: 1,
    guestCount: 2,
    totalAmount: 9486,
    paidAmount: 9486,
    status: 'checkedin',
    paymentStatus: 'paid',
    paymentMethod: 'BANK_CARD',
    bookingTime: '2024-11-28 10:20:00',
    specialRequests: '需要管家服务，安排机场接送',
    source: 'vip',
    rating: null,
    review: null,
    checkInTime: '2024-12-10 14:00:00',
    checkOutTime: null
  },
  {
    id: 'HT20241130001',
    bookingNumber: 'HT20241130001',
    userId: 105,
    userName: '孙七',
    userPhone: '13543219876',
    userEmail: 'sunqi@email.com',
    roomTypeId: 6,
    roomTypeName: '景观大床房',
    roomNumber: '1808',
    checkInDate: '2024-12-05',
    checkOutDate: '2024-12-08',
    nights: 3,
    roomCount: 1,
    guestCount: 2,
    totalAmount: 1938,
    paidAmount: 1938,
    status: 'completed',
    paymentStatus: 'paid',
    paymentMethod: 'WECHAT',
    bookingTime: '2024-11-30 20:15:00',
    specialRequests: '希望房间朝南，景观好一些',
    source: 'online',
    rating: 5,
    review: '景观房的视野真的很棒，房间设施新颖，值得推荐！',
    checkInTime: '2024-12-05 15:45:00',
    checkOutTime: '2024-12-08 12:10:00'
  },
  // 更多历史预订记录
  {
    id: 'HT20241101001',
    bookingNumber: 'HT20241101001',
    userId: 101,
    userName: '张三',
    userPhone: '13912345678',
    userEmail: 'zhangsan@email.com',
    roomTypeId: 4,
    roomTypeName: '标准双床房',
    roomNumber: '0805',
    checkInDate: '2024-11-01',
    checkOutDate: '2024-11-04',
    nights: 3,
    roomCount: 2,
    guestCount: 3,
    totalAmount: 1398,
    paidAmount: 1398,
    status: 'completed',
    paymentStatus: 'paid',
    paymentMethod: 'ALIPAY',
    bookingTime: '2024-10-25 11:30:00',
    specialRequests: '需要相邻房间',
    source: 'online',
    rating: 4,
    review: '房间干净整洁，性价比很高，前台服务热情。',
    checkInTime: '2024-11-01 14:20:00',
    checkOutTime: '2024-11-04 11:45:00'
  }
];

// 房间实体数据（具体房间号）
export const mockRooms = [
  // 豪华海景大床房 (15-25楼)
  { id: 2015, number: '2015', typeId: 1, floor: 20, status: 'occupied', lastCleaned: '2024-12-01 10:30:00' },
  { id: 2016, number: '2016', typeId: 1, floor: 20, status: 'available', lastCleaned: '2024-12-01 11:15:00' },
  { id: 2115, number: '2115', typeId: 1, floor: 21, status: 'available', lastCleaned: '2024-12-01 09:45:00' },
  { id: 2315, number: '2315', typeId: 1, floor: 23, status: 'maintenance', lastCleaned: '2024-11-30 16:20:00' },

  // 商务精英套房 (20-30楼)
  { id: 2508, number: '2508', typeId: 2, floor: 25, status: 'reserved', lastCleaned: '2024-12-01 14:00:00' },
  { id: 2608, number: '2608', typeId: 2, floor: 26, status: 'available', lastCleaned: '2024-12-01 12:30:00' },
  { id: 2808, number: '2808', typeId: 2, floor: 28, status: 'available', lastCleaned: '2024-12-01 13:45:00' },

  // 家庭亲子房 (10-18楼)
  { id: 1205, number: '1205', typeId: 3, floor: 12, status: 'cleaning', lastCleaned: '2024-12-01 08:30:00' },
  { id: 1305, number: '1305', typeId: 3, floor: 13, status: 'available', lastCleaned: '2024-12-01 10:15:00' },
  { id: 1505, number: '1505', typeId: 3, floor: 15, status: 'available', lastCleaned: '2024-12-01 11:45:00' },

  // 总统套房 (30-35楼)
  { id: 3201, number: '3201', typeId: 5, floor: 32, status: 'occupied', lastCleaned: '2024-12-01 09:00:00' },
  { id: 3301, number: '3301', typeId: 5, floor: 33, status: 'available', lastCleaned: '2024-12-01 14:30:00' },

  // 景观大床房 (12-22楼)
  { id: 1808, number: '1808', typeId: 6, floor: 18, status: 'cleaning', lastCleaned: '2024-12-01 07:45:00' },
  { id: 1908, number: '1908', typeId: 6, floor: 19, status: 'available', lastCleaned: '2024-12-01 12:00:00' }
];

// 客户评价数据
export const mockReviews = [
  {
    id: 1,
    bookingId: 'HT20241125001',
    userId: 103,
    userName: '王五',
    userAvatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=80&h=80&fit=crop&crop=face',
    roomTypeId: 3,
    roomTypeName: '家庭亲子房',
    rating: 4,
    review: '家庭房设施很齐全，孩子们很喜欢，特别是儿童游戏区域。房间空间大，卫生间也很干净。前台服务人员很友善，下次还会选择这里。',
    reviewTime: '2024-11-29 10:30:00',
    photos: [
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400&h=300&fit=crop',
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&h=300&fit=crop'
    ],
    helpful: 12,
    tags: ['家庭友好', '儿童设施', '服务优秀']
  },
  {
    id: 2,
    bookingId: 'HT20241201001',
    userId: 101,
    userName: '张三',
    userAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&h=80&fit=crop&crop=face',
    roomTypeId: 1,
    roomTypeName: '豪华海景大床房',
    rating: 5,
    review: '房间很棒，海景非常美，特别是日出的时候。房间设施现代化，床很舒适，浴室也很豪华。酒店服务也很周到，前台小姐姐很热情！强烈推荐！',
    reviewTime: '2024-12-18 16:45:00',
    photos: [
      'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&h=300&fit=crop',
      'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=400&h=300&fit=crop'
    ],
    helpful: 25,
    tags: ['海景绝美', '设施现代', '服务优秀', '强烈推荐']
  },
  {
    id: 3,
    bookingId: 'HT20241130001',
    userId: 105,
    userName: '孙七',
    userAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&crop=face',
    roomTypeId: 6,
    roomTypeName: '景观大床房',
    rating: 5,
    review: '景观房的视野真的很棒，可以俯瞰整个城市夜景。房间装修很有品味，设施齐全。特别喜欢那个大窗户，白天光线很好，晚上看夜景很美。值得推荐！',
    reviewTime: '2024-12-09 14:20:00',
    photos: [
      'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400&h=300&fit=crop'
    ],
    helpful: 18,
    tags: ['景观优美', '装修品味', '夜景绝佳']
  },
  {
    id: 4,
    bookingId: 'HT20241101001',
    userId: 101,
    userName: '张三',
    userAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&h=80&fit=crop&crop=face',
    roomTypeId: 4,
    roomTypeName: '标准双床房',
    rating: 4,
    review: '房间干净整洁，性价比很高。虽然是标准房，但设施应有尽有。前台服务热情，check-in很快。位置也不错，交通便利。',
    reviewTime: '2024-11-05 09:15:00',
    photos: [],
    helpful: 8,
    tags: ['性价比高', '干净整洁', '交通便利']
  }
];

// 酒店统计数据
export const mockStatistics = {
  totalRooms: 135,
  occupiedRooms: 45,
  availableRooms: 78,
  maintenanceRooms: 12,
  occupancyRate: 0.65,
  avgRating: 4.7,
  totalReviews: 1250,
  todayCheckins: 15,
  todayCheckouts: 18,
  todayBookings: 12,
  monthlyRevenue: 1250000,
  yearlyRevenue: 12500000,
  vipCustomers: 156,
  repeatCustomers: 78
};

// 服务项目数据
export const mockServices = [
  {
    id: 1,
    name: '机场接送',
    nameEn: 'Airport Transfer',
    price: 150,
    description: '提供舒适的专车机场接送服务',
    icon: '🚗',
    category: 'transport',
    duration: '单程约45分钟',
    available: true
  },
  {
    id: 2,
    name: 'SPA按摩',
    nameEn: 'SPA Massage',
    price: 280,
    description: '专业理疗师提供放松身心的SPA服务',
    icon: '💆',
    category: 'wellness',
    duration: '60分钟',
    available: true
  },
  {
    id: 3,
    name: '健身房',
    nameEn: 'Fitness Center',
    price: 0,
    description: '24小时开放的现代化健身中心',
    icon: '🏋️',
    category: 'fitness',
    duration: '24小时开放',
    available: true
  },
  {
    id: 4,
    name: '游泳池',
    nameEn: 'Swimming Pool',
    price: 0,
    description: '室内恒温游泳池，提供专业游泳指导',
    icon: '🏊',
    category: 'fitness',
    duration: '6:00-22:00',
    available: true
  },
  {
    id: 5,
    name: '洗衣服务',
    nameEn: 'Laundry Service',
    price: 25,
    description: '专业洗衣熨烫服务，24小时内完成',
    icon: '👔',
    category: 'service',
    duration: '24小时内',
    available: true
  },
  {
    id: 6,
    name: '会议室租赁',
    nameEn: 'Meeting Room Rental',
    price: 500,
    description: '现代化商务会议室，配备投影设备',
    icon: '🏢',
    category: 'business',
    duration: '按小时计费',
    available: true
  }
];

// 导出默认登录用户函数
export const getDefaultLoginUser = (username) => {
  // 如果是管理员账户
  if (username === 'admin') {
    return mockUsers.find(user => user.username === 'admin');
  }

  // 如果是其他预设用户
  const existingUser = mockUsers.find(user => user.username === username);
  if (existingUser) {
    return existingUser;
  }

  // 创建新的普通用户
  return {
    id: Date.now(),
    username: username,
    name: username,
    email: username + '@email.com',
    phone: '138' + Math.random().toString().substr(2, 8),
    role: 'user',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&h=150&fit=crop&crop=face',
    vipLevel: 'bronze',
    totalBookings: 0,
    totalSpent: 0,
    joinDate: new Date().toISOString().split('T')[0],
    preferences: [],
    status: 'active'
  };
};

// 获取用户的预订记录
export const getUserBookings = (userId) => {
  return mockBookings.filter(booking => booking.userId === userId);
};

// 获取房间类型的评价
export const getRoomTypeReviews = (roomTypeId) => {
  return mockReviews.filter(review => review.roomTypeId === roomTypeId);
};

export default {
  mockUsers,
  mockRoomTypes,
  mockBookings,
  mockRooms,
  mockReviews,
  mockStatistics,
  mockServices,
  getDefaultLoginUser,
  getUserBookings,
  getRoomTypeReviews
};