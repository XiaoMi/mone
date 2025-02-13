import type { AxiosError, AxiosProgressEvent, GenericAbortSignal } from 'axios'
import { get, post } from '@/utils/request'
import request from '@/utils/request/axios'
import { useSettingStore } from '@/stores/settings'
import { useUserStore } from '@/stores'
import type { ChatContextMsg } from '@/stores/chatContext'

export function fetchUser() {
  return request({
    url: `/v1/user/getUserInfo`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  }).then(
    ({ data }) => {
      return data
    },
    (error: AxiosError) => {
      const response = error.response
      if (response?.status === 401 && response?.headers?.loginurl) {
        window.location.href = `${response.headers.loginurl}?redirectUrl=${encodeURIComponent(
          window.location.href
        )}`
      } else {
        console.error('no login:', response?.headers)
      }
    }
  )
}

export function logout(tpcToken: string) {
  const { userInfo } = useUserStore()
  const logoutUrl = userInfo.logoutUrl || 'https://login.ozx.yling.top/api-pomission/login/logout'
  return request({
    url: `${logoutUrl}?TPC_TOKEN=${tpcToken}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  }).then(
    () => {
      window.location.href = window.location.origin
    },
    (error: AxiosError) => {
      console.log('logout', error)
      window.location.href = window.location.origin
    }
  )
}

// export function fetchZAddr<T = any>(token: string) {
//   return get<T>({
//     url: '/zAddr',
//     data: { token },
//   })
// }

export function getChatGptModels<T = any>() {
  return get<T>({
    url: `/v1/user/getUserConfig`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchPromptList<T = any>(type: string, token: string) {
  return post<T>({
    url: '/z-open/list',
    data: {
      token,
      type,
      tags: '42'
    }
  })
}

export function fetchChatAPI<T = any>(
  prompt: string,
  options?: { conversationId?: string; parentMessageId?: string },
  signal?: GenericAbortSignal
) {
  return post<T>({
    url: '/chat',
    data: { prompt, options },
    signal
  })
}

export function fetchChatAPIProcess<T = any>(params: {
  chatContextMsg: ChatContextMsg[]
  zToken?: string
  prompt: string
  topicId: string
  signal?: GenericAbortSignal
  model: string
  onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void
}) {
  const setting = useSettingStore()
  let obj = null
  try {
    obj = JSON.parse(params.prompt)
  } catch (e) {
    obj = null
  }
  const date = new Date()
  let data: Record<string, any> = {}
  if (obj && obj.promptName) {
    data = {
      zzToken: params.zToken || '',
      promptName: obj.promptName,
      params: obj.params,
      paramMap: obj.paramMap,
      type: 0,
      id: date.getTime(),
      model: params.model || setting.chatModel || null,
      temperature: setting.temperature,
      from: 'chat'
    }
  } else {
    data = {
      msgList: [
        ...params.chatContextMsg,
        {
          content: params.prompt,
          role: 'USER'
        }
      ],
      topicId: params.topicId,
      currentMsg: params.prompt,
      zzToken: params.zToken || '',
      type: 1,
      id: date.getTime(),
      model: params.model || setting.chatModel || null,
      temperature: setting.temperature,
      from: 'chat'
    }
  }

  return post<T>({
    url: '/v1/chat/ask',
    data,
    signal: params.signal,
    headers: {
      'content-type': 'application/json'
    },
    onDownloadProgress: params.onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchSession<T>() {
  return post<T>({
    url: '/session'
  })
}

export function fetchVerify<T>(token: string) {
  return post<T>({
    url: '/verify',
    data: { token }
  })
}

export function getUserConfig<T = any>(data: {}) {
  return get<T>({
    url: '/v1/user/getUserConfig',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateUserConfig<T = any>(data: {}) {
  return post<T>({
    url: '/v1/user/updateUserConfig',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getModelData<T = any>(data: {}) {
  return get<T>({
    url: '/v1/model/list/v1',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function filePdfUpload<T = any>(data: {}) {
  return post<T>({
    url: '/v1/file/pdf/upload',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
