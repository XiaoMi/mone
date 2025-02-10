import axios from 'axios'

const BASE_URL = 'http://10.220.21.227:8181/audio'

export const audioApi = {
  voiceToText: (audioBase64: string) =>
    axios.post(`${BASE_URL}/audioToText`, {
        audioBase64
    }, {
        headers: {
            "Content-Type": "application/json"
        }
    }),
    textToVoice: (text: string) =>
        axios.post(`${BASE_URL}/textToAudio`, {
            text,
            dialect:"mp3"
        }, {
            headers: {
                "Content-Type": "application/json"
            },
            responseType: 'arraybuffer'
        }),
} 