import { ElMessage } from 'element-plus'
import i18n from '../../lang'

/**
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
    // case '401':
    // case 401:
    //   return false
    default:
      ElMessage({
        message: message || i18n.t('errorMessage.requestError'),
        type: 'error',
        duration: 3000
      })
      return false
  }
}
