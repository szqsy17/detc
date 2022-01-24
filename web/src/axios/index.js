import axios from 'axios'
import qs from 'qs'
import { Loading, Message, Notification } from 'element-ui'
import router from '@/router'
import store from '@/store'

let loading
let isLoading = false

function startLoading() {
  loading = Loading.service({
    lock: true,
    text: '加载中...',
  })
  isLoading = true
}

function endLoading() {
  loading.close()
  isLoading = false
}

function needStringify(url) {
  return url !== '/mirror/upload'
}

function needLoading(url) {
  return url !== '/mirror/upload'
}

axios.defaults.timeout = 30 * 1000
axios.defaults.baseURL = process.env.VUE_APP_URL
axios.defaults.withCredentials = true

// 请求拦截
axios.interceptors.request.use(
  (request) => {
    needLoading(request.url) ? startLoading() : ''
    request.data = needStringify(request.url) ? qs.stringify(request.data, { indices: false }) : request.data
    if (localStorage.t) {
      request.headers.Authorization = 'Bearer ' + localStorage.t
    }
    return request
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截
axios.interceptors.response.use(
  (response) => {
    endLoading()
    let status = response.data.code
    if (status === '401') {
      Message.error('登录过期，请重新登录')
      localStorage.removeItem('t')
      store.dispatch('clearCurrentState').then()
      router.push('/login').then()
      return null
    }

    if (status !== '200') {
      Notification.error({
        title: '',
        duration: 1000,
        message: response.data.msg,
        position: 'bottom-right',
        offset: 30,
      })
    }
    return response
  },
  (error) => {
    endLoading()
    if (error.response) {
      // 后端错误
      if (error.response.status === 200) {
        Message.error(error.response.data.msg)
      } else {
        Message.error('出错了')
        return {
          data: {
            code: '500',
          },
        }
      }
    } else {
      // 前端错误
      if (error.message.code) {
        Message.info(error.message.msg)
        return {
          data: error.message,
        }
      } else {
        Message.error('请检查网络')
        return {
          data: {
            code: '408',
          },
        }
      }
    }

    return Promise.reject(error)
  }
)

export default axios
