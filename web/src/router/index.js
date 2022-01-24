import Vue from 'vue'
import VueRouter from 'vue-router'
import { Message } from 'element-ui'
import jwtDecode from 'jwt-decode'

import Login from '@/views/Login'
import Home from '@/views/Home'
import Unknown from '@/views/Unknown'
import Default from '@/components/MainAnnouncement'
import MainMirrorRelease from '@/components/MainMirrorRelease'
import MainMirrorUpgradeManage from '@/components/MainMirrorUpgradeManage'
import MainMirrorUpgradeStatistic from '@/components/MainMirrorUpgradeStatistic'
import LogManage from '@/components/MainLogManage'
import UserManage from '@/components/MainUserManage'

// const Login = () => import(/* webpackChunkName: "Login", webpackPrefetch: true */ '@/views/Login')
// const Home = () => import(/* webpackChunkName: "Page" */ '@/views/Home')
// const Default = () => import(/* webpackChunkName: "Main" */ '@/components/MainAnnouncement')
// const MainMirrorRelease = () => import(/* webpackChunkName: "Main" */ '@/components/MainMirrorRelease')
// const MainMirrorUpgradeManage = () => import(/* webpackChunkName: "Main" */ '@/components/MainMirrorUpgradeManage')
// const MainMirrorUpgradeStatistic = () =>
//   import(/* webpackChunkName: "Main" */ '@/components/MainMirrorUpgradeStatistic')
// const LogManage = () => import(/* webpackChunkName: "Main" */ '@/components/MainLogManage')
// const UserManage = () => import(/* webpackChunkName: "Main" */ '@/components/MainUserManage')
// const Unknown = () => import(/* webpackChunkName: "Page" */ '@/views/Unknown')

Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err)
}

const routes = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/login',
    component: Login,
  },
  {
    path: '/home',
    component: Home,
    children: [
      {
        path: '',
        component: Default,
      },
      {
        path: 'release',
        component: MainMirrorRelease,
      },
      {
        path: 'upgradeManage',
        component: MainMirrorUpgradeManage,
      },
      {
        path: 'upgradeStatistic',
        component: MainMirrorUpgradeStatistic,
      },
      {
        path: 'log',
        component: LogManage,
      },
      {
        path: 'user',
        component: UserManage,
      },
    ],
  },
  {
    path: '*',
    component: Unknown,
  },
]

const router = new VueRouter({
  mode: 'history',
  routes,
})

router.beforeEach((to, from, next) => {
  let isLogin
  if (localStorage.t) {
    try {
      let decoded = jwtDecode(localStorage.t)

      let expiredTime = decoded.exp * 1000
      let presentTime = new Date().getTime()
      if (expiredTime > presentTime) {
        isLogin = true
      } else {
        isLogin = false
        Message.error('登录过期，请重新登录')
        localStorage.removeItem('t')
      }
    } catch (e) {
      isLogin = false
      Message.error('登录过期，请重新登录')
      localStorage.removeItem('t')
    }
  } else {
    isLogin = false
  }

  if (to.path === '/login') {
    isLogin ? next('/home') : next()
  } else {
    isLogin ? next() : next('/login')
  }
})

export default router
