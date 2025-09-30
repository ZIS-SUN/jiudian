import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Rooms from '@/views/Rooms.vue'
import Booking from '@/views/Booking.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/rooms',
    name: 'Rooms',
    component: Rooms
  },
  {
    path: '/booking',
    name: 'Booking',
    component: Booking
  },
  {
    path: '/my-bookings',
    name: 'MyBookings',
    component: () => import('@/views/MyBookings.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/room-detail',
    name: 'RoomDetail',
    component: () => import('@/views/RoomDetail.vue')
  },
  {
    path: '/promotions',
    name: 'Promotions',
    component: () => import('@/views/Promotions.vue')
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('@/views/Contact.vue')
  },
  {
    path: '/network-test',
    name: 'NetworkTest',
    component: () => import('@/views/NetworkTest.vue')
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'rooms',
        name: 'AdminRooms',
        component: () => import('@/views/admin/Rooms.vue')
      },
      {
        path: 'reservations',
        name: 'Reservations',
        component: () => import('@/views/admin/Reservations.vue')
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('@/views/admin/Customers.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/admin/Announcements.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router