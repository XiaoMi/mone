import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'


export const textToVoice = (text: string) => {
  return Service<IResponse<any>>({
    url: '/audio/textToAudio',
    method: 'post',
    data: {
      text,
      dialect: 'mp3'
    }
  })
}

export const voiceToText = (audioBase64: string) => {
  return Service<IResponse<any>>({
    url: '/audio/audioToText',
    method: 'post',
    data: {
      audioBase64
    }
  })
}