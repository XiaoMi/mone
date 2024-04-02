import { post, get } from '@/utils/request'

interface BOT {
  // 机器人信息
  botInfo: {
    id?: string //机器人id
    name?: string //机器人名称
    workspaceId?: number //工作空间id
    avatarUrl?: string //机器人头像
    creator?: string //创建人
    remark?: string //备注
    permissions?: string //权限
    publishStatus?: string //发布状态
    publishStatusDesc?: string //发布状态描述
  }
  //机器人设置信息
  botSetting: {
    id?: string //id
    botId?: string //机器人id
    setting?: string //机器人设定
    aiModel?: string //模型
    dialogueTurns?: number //对话轮次
    openingRemarks?: string //开场白
    openingQues?: Array<string>
    reservedQue?: Array<string> //预留问题
    timbre?: string //音色
    customizePromptSwitch?: number
    customizePrompt?: string
    timbreSwitch?: number
  }
  botExtensionBo: {
    knowledgeBaseId: any[]
    flowBaseId?: any[]
    pluginId?: any[]
  }
}

// 工作空间
export function getWorkspaceList<T = any>() {
  return get<T>({
    url: '/v1/workspace/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// getProbotList
export function getProbotList<T = any>(data: {
  status?: string
  isMyCollect?: boolean
  pageNum: number
  pageSize: number
  category?: boolean
  name?: boolean
  workspaceId?: string
}) {
  return post<T>({
    url: '/v1/bot/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/json',
      workspaceId: data.workspaceId
    }
  })
}
export function getList4Home2<T = any>() {
  return get<T>({
    url: `/v1/bot/index`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deleteBot<T = any>(data: { botId: number }) {
  return post<T>({
    url: '/v1/bot/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data?.botId
    }
  })
}

// ai 优化
export function promptTurbo<T = any>(
  data?: { prompt: string },
  signal?: any,
  onDownloadProgress?: any
) {
  return post<T>({
    url: '/v1/bot/promptTurbo',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    signal: signal,
    onDownloadProgress: onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// ai 生成
export function aiRecordNotes<T = any>(data?: { botId: string }) {
  return get<T>({
    url: 'v1/bot/publish/aiRecordNotes',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      botId: data?.botId
    }
  })
}

//工作流
export function getFlowList<T = any>(data: {}) {
  return post<T>({
    url: '/v1/flow/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

//插件
export function getBotplugin<T = any>(data: {}) {
  return post<T>({
    url: '/v1/botplugin/org/list',
    data: {
      ...data,
      status: '0'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 创建
export function createBot<T = any>(data: BOT) {
  return post<T>({
    url: '/v1/bot/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      workspaceId: data?.botInfo?.workspaceId
    }
  })
}

//详情
export function getBotDetail<T = any>(data: { botId: string }) {
  return get<T>({
    url: '/v1/bot/getDetail',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      botId: data?.botId
    }
  })
}

/**
 * @description 获取机器人简单信息
 * @param {long} botId
 */
export function getBotSimpleInfo<T = any>(data: { botId: string }) {
  return get<T>({
    url: '/v1/bot/getBotSimpleInfo',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data?.botId
    }
  })
}

// 编辑
export function updateBot<T = any>(data: BOT) {
  return post<T>({
    url: '/v1/bot/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      workspaceId: data?.botInfo?.workspaceId
    }
  })
}

export function publishBot<T = any>(data: {
  id?: string
  botId: string
  versionRecord: string
  publishImChannel: number[]
  categoryIds: number[]
  permissions: number
  openId?: string
}) {
  return post<T>({
    url: '/v1/bot/publish',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      botId: data.botId
    }
  })
}

export function getUserList<T = any>(data: {}) {
  return post<T>({
    url: '/v1/workspace/getUserList',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function searchUser<T = any>(data: {}) {
  return get<T>({
    url: '/v1/user/getUser',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function addUser<T = any>(data: { workspaceId?: number; username?: string; role?: string }) {
  return post<T>({
    url: '/v1/workspace/addUser',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      workspaceId: data?.workspaceId
    }
  })
}

export function deleteUser<T = any>(data: { workspaceId: number; username: string }) {
  return post<T>({
    url: '/v1/workspace/deleteUser',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      workspaceId: data?.workspaceId
    }
  })
}

export function updateUserRole<T = any>(data: {
  workspaceId: number
  username: string
  role: string
}) {
  return post<T>({
    url: '/v1/workspace/updateUserRole',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      workspaceId: data?.workspaceId
    }
  })
}

export function executeBot<T = any>(data: { botId: string; input: string; topicId: string }) {
  return post<T>({
    url: '/v1/bot/execute',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data.botId
    }
  })
}

export function getPresetQuestion<T = any>(data: {
  botId: string
  input: string
  topicId: string
}) {
  return get<T>({
    url: '/v1/bot/getPresetQuestion',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      botId: data.botId
    }
  })
}

// 绑定数据库列表
export function getDatabaseList<T = any>(data: { botId: string; input: string; topicId: string }) {
  return get<T>({
    url: '/v1/bot/getPresetQuestion',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      botId: data.botId
    }
  })
}

// 初始化绑定表数据
export function initFormData(data: {}) {
  return get<T>({
    url: '/v1/ai_table/getTableByBotId',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function uploadBotAvatar<T = any>(data: { base64: string; botId?: number }) {
  return post<T>({
    url: '/v1/image/bot/upload',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data.botId
    }
  })
}

export function createBotAvatar<T = any>(data: { botName: string; botDesc: string }) {
  return get<T>({
    url: '/v1/image/bot/generate',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getAiGenApi(data) {
  return post<T>({
    url: '/v1/ai_table/generate',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 参数列表
export function getBotPluginParameter<T = any>(data?: { id: string }) {
  return get<T>({
    url: '/v1/botplugin/get',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateFormInfo(data) {
  return post<T>({
    url: `/v1/ai_table/updateColumnInfos`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function clearDebuggerMessage<T = any>(data: { botId: string; topicId: string }) {
  return post<T>({
    url: '/v1/bot/clearConversations',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data.botId
    }
  })
}

export function getCommentList<T = any>(data: {
  itemId: string
  type: number
  commentType: string
  pageSize: number
  pageNum: number
}) {
  return get<T>({
    url: '/v1/comment/page',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getCommentDetail<T = any>(data: { itemId: string; type: number }) {
  return get<T>({
    url: '/v1/comment/detail',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function updateComment<T = any>(data: {
  id?: string
  itemId: string
  type: number
  score: number
  commentContent: string
}) {
  return post<T>({
    url: '/v1/comment/insertOrUpdate',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getCommentRates<T = any>(data: { itemId: string; type: number }) {
  return get<T>({
    url: '/v1/comment/statistics',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * @description 获取用户在某个工作空间的角色
 * @param {long} workspaceId
 */
export function getUserWorkspaceRole(data: { workspaceId: number }) {
  return get<T>({
    url: `/v1/workspace/getUserWorkspaceRole`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
