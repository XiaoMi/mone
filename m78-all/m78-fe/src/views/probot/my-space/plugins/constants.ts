import { t } from '@/locales'

const createEnum = <T1, T2, T3>(
  arr: Array<{
    value: T1
    key: T2
    label: T3
  }>
): {
  values: Array<T1>
  keys: Array<T2>
  labels: Array<T3>
  getEnum: (
    value: T1 | T2 | T3,
    type: 'value' | 'key' | 'label'
  ) => {
    value: T1
    key: T2
    label: T3
  }
} => {
  return {
    values: arr.map((v) => v.value),
    keys: arr.map((v) => v.key),
    labels: arr.map((v) => v.label),
    getEnum(value, type) {
      let index
      switch (type) {
        case 'value':
          index = this.values.indexOf(value as T1)
          break
        case 'key':
          index = this.keys.indexOf(value as T2)
          break
        case 'label':
          index = this.labels.indexOf(value as T3)
          break
        default:
          break
      }
      if (index === undefined || index === null) {
        return {
          key: '' as T2,
          label: '' as T3,
          value: undefined as T1
        }
      }
      return {
        key: this.keys[index],
        label: this.labels[index],
        value: this.values[index]
      }
    }
  }
}
/**
 * 插件
 */
export const E_PLUGIN = createEnum([
  {
    value: 0,
    key: 'PUB',
    label: t('plugin.published')
  },
  {
    value: 1,
    key: 'CANCEL',
    label: t('plugin.unpublished')
  }
])

/**
 * 组件
 */
export const E_COMP = createEnum([
  {
    value: 0,
    key: 'ENABLED',
    label: t('plugin.enable')
  },
  {
    value: 1,
    key: 'DISABLED',
    label: t('plugin.unopened')
  }
])

/**
 * 组件调试状态
 */
export const E_DEBUG = createEnum([
  {
    value: 0,
    key: 'UNTESTED',
    label: t('plugin.notDebugged')
  },
  {
    value: 1,
    key: 'TEST_SUCCESS',
    label: t('plugin.debuggingSuccessful')
  },
  {
    value: 2,
    key: 'TEST_FAILED',
    label: t('plugin.debuggingFailed')
  }
])

export const METHOD_LIST = {
  post: '0',
  get: '1',
  put: '2',
  delete: '3',
  head: '4',
  opts: '5',
  patch: '6'
}

export const HEADER = [
  'Accept',
  'Accept-Charset',
  'Accept-Encoding',
  'Accept-Language',
  'Accept-Ranges',
  'Authorization',
  'Cache-Control',
  'Connection',
  'Cookie',
  'Content-Length',
  'Content-Type',
  'Content-MD5',
  'Date',
  'Expect',
  'From',
  'Host',
  'If-Match',
  'If-Modified-Since',
  'If-None-Match',
  'If-Range',
  'If-Unmodified-Since',
  'Max-Forwards',
  'Origin',
  'Pragma',
  'Proxy-Authorization',
  'Range',
  'Referer',
  'TE',
  'Upgrade',
  'User-Agent',
  'Via',
  'Warning',
  'X-User-Agent'
]
