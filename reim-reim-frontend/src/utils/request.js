import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 响应拦截器：统一处理所有业务错误
// 后端 GlobalExceptionHandler 和拦截器均返回 HTTP 200 + body.code 区分结果
// code === 200 → 成功，其余均为业务异常，由拦截器统一弹窗提示
request.interceptors.response.use(
  (response) => {
    const { code, message } = response.data
    if (code !== 200) {
      // 429 用 warning（防重复提交），其余用 error
      if (code === 429) {
        ElMessage.warning(message || '操作过于频繁，请勿重复提交')
      } else {
        ElMessage.error(message || '操作失败')
      }
      return Promise.reject(new Error(message))
    }
    return response
  },
  () => {
    ElMessage.error('网络异常，请检查网络连接')
    return Promise.reject(new Error('网络异常'))
  }
)

export default request