import { ElMessage } from 'element-plus'

/**
 * @url https://xiaomi.f.mioffice.cn/docs/dock4A0OIZ4F6V79JuIEIXgdaDg
 * @param code 状态码
 * @param message 错误信息
 * @returns boolean
 */
export default function ({ code, message, status }) {
  if (code !== 0) {
    code = code || status
  }
  switch (code) {
    case '0':
    case 0:
    case '200':
    case 200:
      return true
    case '401':
    case 401:
      ElMessage({
        message: "无权限",
        type: 'error',
        duration: 3000,
        onClose () {
          // 重定向
        }
      })
      return false
    default:
      ElMessage({
        message: "请求错误",
        type: 'error',
        duration: 3000
      })
      return false
  }
}
