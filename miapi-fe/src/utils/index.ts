export { default as Cookie } from './cookie'
import { outerHostName } from "../views/constant"

/**
 * @description 获取Query参数值
 * @param {string} key - 参数key
 * @param {string} url - 默认当前地址
 */
export const getQuery = (key:string, url?:string) => {
  url = url || window.location.href + ''
  if (url.indexOf('#') !== -1) url = url.substring(url.indexOf('#'))
  let rts = []
  let rt
  const queryReg = new RegExp('(^|\\?|&)' + key + '=([^&]*)(?=&|#|$)', 'g')
  while ((rt = queryReg.exec(url)) !== null) {
    rts.push(decodeURIComponent(rt[2]))
  }
  if (rts.length === 0) return null
  if (rts.length === 1) return rts[0]
  return rts
}

/**
 * @description 对象数组去重
 * @param {Array} arr - 原数组
 * @param {string} key - 依赖key
 */
export const unique = (arr, key) => {
  if (!arr) return arr
  if (key === undefined) return [...new Set(arr)]
  const map = {
    'string': e => e[key],
    'function': e => key(e)
  }
  const fn = map[typeof key]
  // eslint-disable-next-line no-return-assign
  const obj = arr.reduce((o, e) => (o[fn(e)] = e, o), {})
  return Object.values(obj)
}

/**
 * @description 对象数组去重
 * @param {Array} arr - 原数组
 * @param {string} key - 依赖key
 */
export const deRepeat = (arr, key) => {
  let map = new Map()
  for (let item of arr) {
    if (!map.has(item[key])) {
      map.set(item[key], item)
    }
  }
  return [...map.values()]
}

/**
 * @description 判但是否是英文
 */
export const languageIsEN = () => {
  return window.localStorage.getItem('apiLocaleLanguage') === 'en'
}

/**
 * @description 深度解析字符串对象
 */
export const jsonParse = (data) => {
  if (typeof data === 'string') {
    try {
      data = JSON.parse(data)
    } catch (error) {}
  }
  data = deepParse(data)
  return data
}

/**
 * @description 深度解析字符串对象
 */
export const deepParse = (item) => {
  let arg
  if (Array.isArray(item)) {
    arg = [].concat(item)
    arg = arg.map(v => jsonParse(v))
  } else if (Object.prototype.toString.call(item) === '[object Object]') {
    arg = { ...item }
    Object.keys(arg).forEach(key => {
      arg[key] = jsonParse(arg[key])
    })
  } else {
    arg = item
  }
  return arg
}

/**
 * @description 比较是否在指定时间之间--[)
 */
export const diffDate = (pre, next) => {
  let now = new Date().getTime()
  return new Date(pre).getTime() <= now && new Date(next).getTime() > now
}

/**
 * @description 判断元素是否出现在可视区
 */
export const isInToView = (el, scrollDom = document.documentElement) => {
  const viewPortHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
  const offsetTop = el.offsetTop
  const scrollTop = scrollDom.scrollTop
  const top = offsetTop - scrollTop
  return top <= viewPortHeight
}

/**
 * @description 判断是否是外网
 */
export const isExtranet = false
// export const isExtranet = window.location.hostname === outerHostName
