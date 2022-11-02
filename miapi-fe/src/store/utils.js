
import { PROTOCOL_TYPE, DATA_TYPE_KEY, DATA_TYPE } from '@/views/constant'
import { ElMessage } from 'element-plus'
import { unique } from '@/utils'
import i18n from '../lang'

// api系统默认分组放第一位 start
export function sortGroupList (arr = []) {
  let a = []
  arr.forEach(v => {
    if (v.systemGroup) {
      a.unshift(v)
    } else {
      a.push(v)
    }
  })
  return a
}
// api系统默认分组放第一位 end

// api提交校验 start
let haveSameKey = false
let deepLoopParentType = ''

export const handleFilter = (arr, type) => {
  if (!Array.isArray(arr)) {
    return arr
  }
  let index = []
  switch (type) {
    case 'dubbo':
      for (let i = 0; i < arr.length; i++) {
        if (!arr[i].itemName && !arr[i].itemClassStr) {
          index.push(i)
        } else if (Array.isArray(arr[i].itemValue)) {
          handleFilter(arr[i].itemValue, type)
        }
      }
      break
    default:
      for (let i = 0; i < arr.length; i++) {
        if (!arr[i].paramKey && !arr[i].paramType) {
          index.push(i)
        } else if (Array.isArray(arr[i].childList)) { // formjson
          handleFilter(arr[i].childList, type)
        } else if (Array.isArray(arr[i].paramValue)) { // form
          handleFilter(arr[i].paramValue, type)
        }
      }
      break
  }
  index.reverse().forEach(j => {
    arr.splice(j, 1)
  })
  return arr
}
export const checkHaveSameKey = (arg, isDubbo = false) => {
  if (!Array.isArray(arg)) {
    return haveSameKey
  }
  if (isDubbo) {
    checkHaveSameKeyDiff(arg, 'itemName', isDubbo)
  } else {
    checkHaveSameKeyDiff(arg, 'paramKey', isDubbo)
  }
  setTimeout(() => {
    haveSameKey = false
  }, 0)
  return haveSameKey
}

export const checkHaveSameKeyDiff = (arg, key, isDubbo) => {
  arg = arg.map((v, index) => {
    if (!v[key]) {
      return {
        ...v,
        [key]: `${key}${index}`
      }
    }
    return v
  })
  let bool = true
  let arr = unique(arg, key)
  bool = arg.length !== arr.length
  if (bool) {
    haveSameKey = bool
  } else {
    for (let i = 0; i < arr.length; i++) {
      if (haveSameKey) {
        break
      }
      if (!isDubbo && Array.isArray(arr[i].paramValue)) { // form
        checkHaveSameKeyDiff(arr[i].paramValue, key, isDubbo)
      } else if (!isDubbo && Array.isArray(arr[i].childList)) { // formjson
        checkHaveSameKeyDiff(arr[i].childList, key, isDubbo)
      } else if (isDubbo && Array.isArray(arr[i].itemValue)) {
        checkHaveSameKeyDiff(arr[i].itemValue, key, isDubbo)
      }
    }
  }
}
export const handleDeepCheck = (arr) => {
  let bool = false
  if (!Array.isArray(arr)) {
    return bool
  }
  for (let i = 0; i < arr.length; i++) {
    if ((deepLoopParentType !== DATA_TYPE_KEY.array && deepLoopParentType !== DATA_TYPE_KEY.dubboArray) && (!arr[i].paramKey || !arr[i].paramType)) {
      bool = true
      break
    } else if ((deepLoopParentType === DATA_TYPE_KEY.array) && !arr[i].paramType) {
      bool = true
      break
    } else if (Array.isArray(arr[i].childList)) {
      if (deepLoopParentType !== DATA_TYPE_KEY.array && deepLoopParentType !== DATA_TYPE_KEY.dubboArray && (arr[i].paramType !== DATA_TYPE_KEY.json && arr[i].paramType !== DATA_TYPE_KEY.object)) {
        deepLoopParentType = arr[i].paramType
      }
      bool = handleDeepCheck(arr[i].childList)
    } else if (Array.isArray(arr[i].paramValue)) {
      if (deepLoopParentType !== DATA_TYPE_KEY.array && (arr[i].paramType !== DATA_TYPE_KEY.json && arr[i].paramType !== DATA_TYPE_KEY.object)) {
        deepLoopParentType = arr[i].paramType
      }
      bool = handleDeepCheck(arr[i].paramValue)
    }
  }
  return bool
}
export const handleCheckValue = (param, addApiProtocol) => {
  if (
    !param.groupID && addApiProtocol === PROTOCOL_TYPE.HTTP
  ) {
    ElMessage.error(i18n.t('placeholder.selectGroup'))
    return true
  } else if (
    !param.apiName && addApiProtocol === PROTOCOL_TYPE.HTTP
  ) {
    ElMessage.error(i18n.t('errorMessage.pleaseFillName'))
    return true
  } else if (
    !param.apiURI && addApiProtocol === PROTOCOL_TYPE.HTTP
  ) {
    ElMessage.error(i18n.t('errorMessage.pleaseFillUrl'))
    return true
  } else if (
    (addApiProtocol !== PROTOCOL_TYPE.Dubbo && addApiProtocol !== PROTOCOL_TYPE.Grpc) &&
    param.apiHeader.some(item => !item.headerName || !item.headerValue)
  ) {
    ElMessage.error(i18n.t('errorMessage.incorrectRequestHeaderFormat'))
    return true
  } else if (
    checkHaveSameKey(param.apiRequestParam, addApiProtocol === PROTOCOL_TYPE.Dubbo)
  ) {
    ElMessage.error(i18n.t('errorMessage.parameterNameNotRepeated'))
    haveSameKey = false
    return true
  } else if (
    (addApiProtocol !== PROTOCOL_TYPE.Dubbo && addApiProtocol !== PROTOCOL_TYPE.Grpc) &&
    handleDeepCheck(param.apiRequestParam)
  ) {
    deepLoopParentType = ''
    ElMessage.error(i18n.t('errorMessage.incorrectRequestParameterFormat'))
    return true
  } else if (
    (addApiProtocol !== PROTOCOL_TYPE.Dubbo && addApiProtocol !== PROTOCOL_TYPE.Grpc) &&
    handleDeepCheck(param.apiResultParam)
  ) {
    deepLoopParentType = ''
    ElMessage.error(i18n.t('errorMessage.returnParameterFormatIncorrect'))
    return true
  } else if (
    checkHaveSameKey(param.apiResultParam, addApiProtocol === PROTOCOL_TYPE.Dubbo)
  ) {
    ElMessage.error(i18n.t('errorMessage.returnParameterNameNotRepeated'))
    haveSameKey = false
    return true
  }
  deepLoopParentType = ''
  return false
}
// api提交校验 end

// 获取对应api所在项目及分组名称 start
/**
 * @description 获取对应api所在项目及分组名称
 * @param {array} projectList - 项目列表
 * @param {array} groupList - 分组列表
 * @param {number} projectID - 项目id
 * @param {number} groupID - 分组id
 */
export const findProjectAndGroup = ({
  projectList = [],
  groupList = [],
  projectID,
  groupID
}) => {
  let projectName = ''
  let groupName = ''
  if (projectList && projectList.length) {
    let target = projectList.fitter(v => v.projectID === Number(projectID))
    projectName = target[0] ? target[0].projectName || target[0].name : ''
  }
  if (groupList && groupList.length) {
    let target = groupList.fitter(v => v.groupID === Number(groupID))
    groupName = target[0] ? target[0].groupName : ''
  }
  return { projectName, groupName }
}
// 获取对应api所在项目及分组名称 end

let DATA_TYPE_EX = {}

Object.keys(DATA_TYPE).forEach(key => {
  DATA_TYPE_EX[DATA_TYPE[key].replace(/\[|\]/g, '')] = key
})

const handleDeepData = (v) => {
  let obj = {}
  let arr = []
  switch (v.paramType) {
    case DATA_TYPE_EX.int:
    case DATA_TYPE_EX.double:
    case DATA_TYPE_EX.float:
    case DATA_TYPE_EX.datetime:
    case DATA_TYPE_EX.long:
    case DATA_TYPE_EX.number:
      return Number(v.paramValue)
    case DATA_TYPE_EX.boolean:
      if (v.paramValue === "false") {
        return false
      } else if (v.paramValue === "true") {
        return true
      }
      return !!v.paramValue
    case '15': // gateway request
    case DATA_TYPE_EX.array:
      (v.childList || []).forEach(item => {
        arr.push(handleDeepData(item))
      })
      return arr
    case DATA_TYPE_EX.object:
    case '2': // 老数据 json
      (v.childList || []).forEach(item => {
        obj[item.paramKey] = handleDeepData(item)
      })
      return obj
    default:
      return v.paramValue
  }
}

export const copyParam = (req = []) => {
  let obj = {}
  if (!Array.isArray(req)){
    return obj;
  }
  req.forEach(v => {
    obj[v.paramKey] = handleDeepData(v)
  })
  let keys = Object.keys(obj)
  if (keys.length === 1 && keys[0] === 'root') {
    return obj.root
  }
  if (keys.length === 1 && keys[0] === 'rootArr') {
    return obj.rootArr
  }
  return obj
}

export const getDataType = (v) => {
  switch (Object.prototype.toString.call(v)) {
    case '[object Null]':
    case '[object Undefined]':
    case '[object Number]':
      return DATA_TYPE_KEY.number
    case '[object Boolean]':
      return DATA_TYPE_KEY.boolean
    case '[object Object]':
      return DATA_TYPE_KEY.object
    case '[object Array]':
      return DATA_TYPE_KEY.array
    default:
      if (/^[0-9]*$/g.test(v)) {
        return DATA_TYPE_KEY.number
      } else {
        return DATA_TYPE_KEY.string
      }
  }
}
