import { post, get } from '@/utils/request'

export function speechToText<T = any>(data: { text: string; format: string }) {
  return post<T>({
    url: 'speechToText2',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL1
  })
}

export function fetchTextToAudio<T = any>(data: { text: string; dialect?: string }) {
  return fetch(import.meta.env.VITE_GLOB_API_NEW_URL + 'v1/audio/textToAudio', {
    method: 'post',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((res) => {
    return res.blob()
  })
}
