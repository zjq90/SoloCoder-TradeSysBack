import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'HomeFilled' }
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: '用户管理', icon: 'User' }
          },
          {
            path: 'role',
            name: 'Role',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: '角色管理', icon: 'UserFilled' }
          },
          {
            path: 'permission',
            name: 'Permission',
            component: () => import('@/views/system/permission/index.vue'),
            meta: { title: '权限管理', icon: 'Lock' }
          }
        ]
      },
      {
        path: 'agent',
        name: 'Agent',
        component: () => import('@/views/agent/index.vue'),
        meta: { title: '代理商管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'merchant',
        name: 'Merchant',
        component: () => import('@/views/merchant/index.vue'),
        meta: { title: '商户管理', icon: 'Shop' }
      },
      {
        path: 'machine',
        name: 'Machine',
        component: () => import('@/views/machine/index.vue'),
        meta: { title: '机器管理', icon: 'Monitor' }
      },
      {
        path: 'channel',
        name: 'Channel',
        component: () => import('@/views/channel/index.vue'),
        meta: { title: '通道管理', icon: 'Connection' }
      },
      {
        path: 'transaction',
        name: 'Transaction',
        component: () => import('@/views/transaction/index.vue'),
        meta: { title: '交易明细', icon: 'List' }
      },
      {
        path: 'profit-share',
        name: 'ProfitShare',
        component: () => import('@/views/profit-share/index.vue'),
        meta: { title: '分润管理', icon: 'Money' }
      },
      {
        path: 'statement',
        name: 'Statement',
        component: () => import('@/views/statement/index.vue'),
        meta: { title: '对账单', icon: 'Document' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  document.title = to.meta.title ? `${to.meta.title} - 交易管理系统` : '交易管理系统'

  if (to.path === '/login') {
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
  } else {
    if (userStore.isLoggedIn) {
      if (!userStore.userInfo) {
        await userStore.getUserInfo()
      }
      next()
    } else {
      next('/login')
    }
  }
})

export default router
