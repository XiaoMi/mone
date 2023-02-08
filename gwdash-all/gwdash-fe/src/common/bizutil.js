/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

let bizutil = {}

/**
 * 用于将状态值转换为对应的显示值
 * @param {int} status
 */
bizutil.roleConv = (role) => {
  let ret = role
  switch (role) {
    case 1:
      ret = 'Admin'
      break
    case 2:
      ret = 'Work'
      break
  }
  return ret
}

/**
 * 用于将布尔值转换为对应的显示值
 * @param {int} val
 */
bizutil.boolDisplay = (val) => {
  let ret = val
  switch (val) {
    case 0:
      ret = '否'
      break
    case 1:
      ret = '是'
      break
  }
  return ret
}

/**
 * 用于将布尔值转换为对应的显示值
 * @param {int} val
 */
bizutil.booleanDisplay = (val) => {
  let ret = val
  if (val) {
    return '是'
  }

  return '否'
}

/**
 * 用于将路由类型转换为对应的显示值
 * @param {int} val
 */
bizutil.routeTypeDisplay = (val) => {
  let ret = val
  switch (val) {
    case 0:
      ret = 'HTTP'
      break
    case 1:
      ret = 'xx_replace_xxDUBBO'
      break
    case 2:
      ret = 'API_COMPOSE'
      break
    case 3:
      ret = 'PLUGIN'
      break
    case 4:
      ret = 'DUBBO'
      break
    default:
      ret = '未知'
  }
  return ret
}

/**
 * 用于将脚本执行类型转换为对应的显示值
 * @param {int} val
 */
bizutil.scriptTypeDisplay = (val) => {
  let ret = val
  switch (val) {
    case 0:
      ret = '直接执行'
      break
    case 1:
      ret = '前置'
      break
    case 2:
      ret = '后置'
      break
    case 3:
      ret = '包裹'
      break
    default:
      ret = '未知'
  }
  return ret
}

/**
 * 将毫秒值转为时间字符串
 * @param {int} val
 */
bizutil.timeFormat = (val) => {
  if (val <= 0) return val
  let ret = ''
  let date = new Date(val)
  let Y = date.getFullYear()
  let M = date.getMonth() + 1
  let D = date.getDate()
  let H = date.getHours()
  let m = date.getMinutes()
  let s = date.getSeconds()
  // 月/天/时/分/秒小于10前面补0
  if (M < 10) {
    M = '0' + M
  }
  if (D < 10) {
    D = '0' + D
  }
  if (H < 10) {
    H = '0' + H
  }
  if (m < 10) {
    m = '0' + m
  }
  if (s < 10) {
    s = '0' + s
  }
  ret = Y + '-' + M + '-' + D + ' ' + H + ':' + m + ':' + s
  return ret
}

/**
 *
 * @param val :2018-10-25T16:46:46+08:00
 * @returns {string|string}
 */
bizutil.timeStrFormat = (val) => {
  val = val.toString()
  let ret = val.replace(/T/, ' ')
  ret = ret.replace('+08:00', '')
  return ret
}

bizutil.opts = {
  status: [
    {
      value: 0,
      label: 'A'
    },
    {
      value: 1,
      label: 'B'
    },
    {
      value: 2,
      label: 'C'
    },
    {
      value: 3,
      label: 'D'
    }
  ],
  bools: [
    {
      value: 0,
      label: '否'
    },
    {
      value: 1,
      label: '是'
    }
  ]
}

/**
 * 处理账户列表数据
 * @param accounts
 * @returns {Array}
 */
bizutil.accountProcess = (accounts) => {
  if (accounts === undefined || !Array.isArray(accounts)) {
    return []
  }

  let ret = []

  accounts.forEach(account => {
    account.roleDisplay = bizutil.roleConv(account.role)
    account.ctimeDisplay = bizutil.timeFormat(account.ctime)
    account.utimeDisplay = bizutil.timeFormat(account.utime)
    account.role = roleFormat(account.role)
    ret.push(account)
  })
  return ret
}

/**
 * 处理API分组列表数据
 * @param groups
 * @returns {Array}
 */
bizutil.apiGroupListProcess = (groups) => {
  if (groups === undefined || !Array.isArray(groups)) {
    return []
  }

  let ret = []
  groups.forEach(c => {
    c.ctime = bizutil.timeFormat(c.ctime)
    c.utime = bizutil.timeFormat(c.utime)
    ret.push(c)
  })
  return ret
}

/**
 * 处理API列表数据
 * @param apis
 * @returns {Array}
 */
bizutil.apiListProcess = (apis) => {
  if (apis === undefined || !Array.isArray(apis)) {
    return []
  }

  let ret = []
  apis.forEach(c => {
    c.routeTypeHttp = (c.routeType === 0)
    c.routeTypeDubbo = (c.routeType === 1)
    c.routeTypeCompose = (c.routeType === 2)
    c.routeTypeDisplay = bizutil.routeTypeDisplay(c.routeType)
    c.scriptTypeDisplay = bizutil.scriptTypeDisplay(c.scriptType)
    c.allowCacheDisplay = bizutil.booleanDisplay(c.allowCache)
    c.allowPreviewDisplay = bizutil.booleanDisplay(c.allowPreview)
    c.allowAuthDisplay = bizutil.booleanDisplay(c.allowAuth)
    c.offlineDisplay = bizutil.booleanDisplay(c.offline)
    c.allowLogDisplay = bizutil.booleanDisplay(c.allowLog)
    c.allowCorsDisplay = bizutil.booleanDisplay(c.allowCors)
    c.allowMockDisplay = bizutil.booleanDisplay(c.allowMock)
    c.allowScriptDisplay = bizutil.booleanDisplay(c.allowScript)
    c.allowTokenDisplay = bizutil.booleanDisplay(c.allowToken)
    c.useQpsLimitDisplay = bizutil.booleanDisplay(c.useQpsLimit)
    if (c.paramTemplate === '' || c.paramTemplate === undefined) {
      c.paramTemplate = '[]'
    }
    c.ctime = bizutil.timeFormat(c.ctime)
    c.utime = bizutil.timeFormat(c.utime)
    ret.push(c)
  })
  return ret
}

/**
 * 处理API DUBBO参数模板
 * @param apis
 * @returns {Array}
 */
bizutil.apiDubboParamTplProcess = (tplStr) => {
  if (tplStr === undefined || tplStr === '') {
    return []
  }

  let tpls = JSON.parse(tplStr)

  if (tpls === undefined || !Array.isArray(tpls)) {
    return []
  }

  let ret = []
  let idx = 1
  tpls.forEach(c => {
    c.itemNo = idx
    idx++
    ret.push(c)
  })
  return ret
}

/**
 * 处理AGENT列表数据
 * @param agents
 * @returns {Array}
 */
bizutil.agentListProcess = (agents) => {
  if (agents === undefined || !Array.isArray(agents)) {
    return []
  }

  let ret = []
  agents.forEach(c => {
    c.ctime = bizutil.timeFormat(c.ctime)
    c.utime = bizutil.timeFormat(c.utime)
    ret.push(c)
  })
  return ret
}

bizutil.processDagData = (groupConfig) => {
  if (!groupConfig) {
    return {
      data: [],
      edg: []
    }
  }

  let data = []
  let edg = []
  if (groupConfig.taskList && groupConfig.taskList.length) {
    groupConfig.taskList.forEach(d => {
      let nd = {
        id: d.index,
        label: d.data.url
      }
      data.push(nd)
    })
  }

  if (groupConfig.dependList && groupConfig.dependList.length) {
    groupConfig.dependList.forEach(e => {
      let ne = {
        start: e.from,
        end: e.to
      }
      edg.push(ne)
    })
  }
  return {
    data: data,
    edg: edg
  }
}

/**
 * 用于从多行数据中获取id列表
 * @param val
 * @returns {Array}
 */
bizutil.getBatchIdsForOpt = (val) => {
  if (val === undefined || !Array.isArray(val)) {
    return []
  }

  let ids = []
  val.forEach(element => {
    ids.push(element.id)
  })
  return ids
}

/**
 * 小数转换为百分比
 * @param val
 * @returns {String}
 */
bizutil.toPercent = (val) => {
  if (val === 0) {
    return '00.00%'
  }
  var str = Number(val * 100).toFixed(2)
  str += '%'
  return str
}

bizutil.toPercentage = (val) => {
  if (val === 0) {
    return '00.00%'
  }
  var str = Number(val).toFixed(2)
  str += '%'
  return str
}

/**
 * 实现复制文本功能
 * @param val
 * @returns {Boolean}
 */
bizutil.copyText = (val) => {
  var result = false
  var oInput = document.createElement('input')
  oInput.value = val
  document.body.appendChild(oInput)
  oInput.select()
  result = document.execCommand('Copy')
  oInput.remove()
  return result
}

/**
 * 格式化时间
 * @param 时间戳
 * @returns {String}
 */
bizutil.formatNow = now => {
  var time = new Date(now)
  var year = time.getFullYear()
  var month = time.getMonth() + 1
  var day = time.getDate()
  var hour = time.getHours()
  var minutes = time.getMinutes()
  return `${year}/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`
}

bizutil.toRetain = (val) => {
  if (val === 0) {
    return 0
  }
  var str = Number(val).toFixed(2)
  return str
}

const roleFormat = (val) => {
  return val === undefined ? val : val.toString()
}

const boolFormat = (val) => {
  return val === undefined ? val : val.toString()
}

const showDataBtn = (val) => {
  if (val === undefined || val.trim().length === 0) {
    return false
  }
  return true
}

const scriptFormat = (val) => {
  if (val === undefined) {
    return ''
  }
  return val
}
export default bizutil
