import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Root',
    redirect: () => {
      const userStore = useUserStore()
      // 根据用户角色重定向
      if (userStore.isLoggedIn) {
        return userStore.isAdmin ? '/admin' : '/home'
      }
      return '/login'
    }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/notice',
    name: 'NoticeList',
    component: () => import('@/views/notice/NoticeList.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/notice/:id',
    name: 'NoticeDetail',
    component: () => import('@/views/notice/NoticeDetail.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/complaint',
    name: 'ComplaintList',
    component: () => import('@/views/complaint/ComplaintList.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/complaint/create',
    name: 'ComplaintCreate',
    component: () => import('@/views/complaint/ComplaintCreate.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/study',
    name: 'StudyList',
    component: () => import('@/views/study/StudyList.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/study/:id',
    name: 'StudyDetail',
    component: () => import('@/views/study/StudyDetail.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/study/favorite',
    name: 'StudyFavorite',
    component: () => import('@/views/study/StudyFavorite.vue'),
    meta: { requiresAuth: true, requiresVillager: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  // 村干部后台路由
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue')
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/NoticeManage.vue')
      },
      {
        path: 'complaints',
        name: 'AdminComplaints',
        component: () => import('@/views/admin/ComplaintManage.vue')
      },
      {
        path: 'study',
        name: 'AdminStudy',
        component: () => import('@/views/admin/StudyManage.vue')
      },
      {
        path: 'people',
        name: 'AdminPeople',
        component: () => import('@/views/admin/PeopleManage.vue')
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
  const userStore = useUserStore()

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin) {
    if (!userStore.isAdmin) {
      ElMessage.error('权限不足，村民无法访问管理页面')
      next('/home')
      return
    }
  }

  // 需要村民权限的页面（村干部不能访问）
  if (to.meta.requiresVillager) {
    if (userStore.isAdmin) {
      ElMessage.error('村干部请使用管理后台')
      next('/admin')
      return
    }
  }

  next()
})

export default router
