import { Service, type IResponse } from '@/common/req'

export function executeBot<T = any>(data: {
  botId: string
  input: string
  topicId: string
}): Promise<IResponse<T>> {
  return Service({
    url: '/v1/bot/execute',
    method: 'post',
    data,
    baseURL: '/api',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      botId: data.botId,
    },
    timeout: 120000,
  })
}

export function getBotDetail<T = any>(params: { botId: string }): Promise<IResponse<any>> {
  return Service({
    url: '/v1/bot/getDetail',
    method: 'get',
    params,
    baseURL: '/api',
    headers: {
      botId: params?.botId,
    },
  })
}

export function getPresetQuestion<T = any>(params: { botId: string }): Promise<IResponse<any>> {
  return Service({
    url: '/v1/bot/getPresetQuestion',
    method: 'get',
    params,
    baseURL: '/api',
    headers: {
      botId: params?.botId,
    },
  })
}

export function getRoles<T = any>(): Promise<
  IResponse<{
    id: number
    name: string
    age: number
    gender: string
    description: string
    personality: string
    roleType: string
    voice: string
  }>
> {
  return Service({
    url: '/api/buddy/getRoles',
    method: 'post',
    baseURL: '/dev',
  })
}

export function speechToText<T = any>(data: {
  text: string
  format: string
}): Promise<IResponse<string>> {
  return Service({
    url: '/audio/audioToText2',
    data: {
      audioBase64: data.text,
    },
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
    },
    baseURL: '/dev',
  })
}

export function fetchTextToAudio(data: { text: string; voice: string }) {
  return fetch('/dev/audio/textToAudio', {
    method: 'post',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json',
    },
  }).then((res) => {
    return res.blob()
  })
}
