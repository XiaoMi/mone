import { post, get } from '@/utils/request'

export function fetchTranslateType<T = any>(data: { content: string }) {
  return post<T>({
    url: '/v1/translate/language/type/analyze',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchAudioToText<T = any>(data: {}) {
  return post<T>({
    url: '/v1/translate/audioToText',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
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

export function fetchTranslateFavorite<T = any>(uuid: string) {
  return post<T>({
    url: '/v1/translate/favorite',
    data: {
      uuid
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchTranslateUnfavorite<T = any>(uuid: string) {
  return post<T>({
    url: '/v1/translate/unfavorite',
    data: {
      uuid
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// export function fetchImagetStream<T = any>(data: {}) {
//   return post<T>({
//     url: '/v1/image/stream',
//     data,
//     baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
//   })
// }
export function fetchImageStream<T = any>(data?: any, signal?: any, onDownloadProgress?: any) {
  return post<T>({
    url: '/v1/image/stream',
    data,
    signal: signal,
    onDownloadProgress: onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function fetchDocStream<T = any>(data?: any, signal?: any, onDownloadProgress?: any) {
  return post<T>({
    url: '/v1/translate/uploadDocument',
    data,
    signal: signal,
    onDownloadProgress: onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchTranslateHistoryList<T = any>(data: { type: number }) {
  return post<T>({
    url: '/v1/translate/history',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchTranslateHistoryDelete<T = any>(data: { uuid: string }) {
  return post<T>({
    url: '/v1/translate/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function fetchTranslateChat<T = any>(data: {}, signal) {
  return post<T>({
    url: '/v1/translate/text/chat',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    signal
  })
}

export function fetchTranslateFavorites<T = any>(data: {}) {
  return get<T>({
    url: '/v1/translate/favorites',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
