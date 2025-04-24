<template>
  <div class="microphone-container">
    <div class="microphone">
      <div class="mic-icon">
        <el-icon size="50px"><Mic /></el-icon>
      </div>
      <div class="wave-container">
        <div class="wave"></div>
        <div class="wave"></div>
        <div class="wave"></div>
      </div>
    </div>
    <div v-if="!hasPermission" class="permission-tip">
      请允许使用麦克风权限
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { v4 as uuidv4 } from 'uuid';
import { wsUtil } from '@/api/wsUtils';

const mediaRecorder = ref<MediaRecorder | null>(null)
const audioChunks = ref<Blob[]>([])
const isRecording = ref(false)
const hasPermission = ref(false)
const socket = ref<WebSocket | null>(null)
const uuid = ref<string>(uuidv4());
let recordingInterval: number
const sessionUpdate = {
    "event_id": uuid.value,
    "type": "session.update",
    "session": {
        "modalities": ["audio"],
        "instructions": "在你的声音中注入情感，经常大笑",
        "voice": "female-yujie-jingpin",
        "input_audio_format": "pcm16",
        "output_audio_format": "pcm16",
        "temperature": 0.8,
        "max_response_output_tokens": "10000"
    }
}

const audioAppend = {
  "event_id": uuid.value,
  "type": "input_audio_buffer.append",
  "audio": ""
}

const audioCommit = {
  "event_id": uuid.value,
  "type": "input_audio_buffer.commit",
}

const audioClear = {
  "event_id": uuid.value,
  "type": "input_audio_buffer.clear",
}

const checkMicrophonePermission = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    stream.getTracks().forEach(track => track.stop())
    hasPermission.value = true
    return true
  } catch (error) {
    console.error('麦克风权限检查失败:', error)
    hasPermission.value = false
    return false
  }
}

const startRecording = async () => {
  try {
    const permissionGranted = await checkMicrophonePermission()
    if (!permissionGranted) {
      return
    }
    
    const stream = await navigator.mediaDevices.getUserMedia({ 
      audio: {
        channelCount: 1,
        sampleRate: 16000,
        sampleSize: 16,
      } 
    })
    mediaRecorder.value = new MediaRecorder(stream, {
      mimeType: 'audio/webm;codecs=pcm',
      audioBitsPerSecond: 16000
    })
    
    mediaRecorder.value.ondataavailable = (event) => {
      if (event.data.size > 0) {
        audioChunks.value.push(event.data)
      }
    }

    mediaRecorder.value.onstop = () => {
      const audioBlob = new Blob(audioChunks.value, { type: 'audio/webm;codecs=pcm' })
      convertToBase64(audioBlob)
      audioChunks.value = []
    }

    isRecording.value = true
    mediaRecorder.value.start()
  } catch (error) {
    console.error('获取麦克风权限失败:', error)
    hasPermission.value = false
  }
}

const wsConnect = () => {
  socket.value = wsUtil("",() => {
    socket.value?.send(JSON.stringify(sessionUpdate));
  },() => {
    socket.value = null;
  },(data: any) => {
    console.log('WebSocket connection closed:', data)
  });
}

const convertToBase64 = async (blob: Blob) => {
  const reader = new FileReader()
  reader.onloadend = () => {
    audioAppend.audio = reader.result as string;
    socket.value?.send(JSON.stringify(audioAppend));
    console.log('音频Base64:', reader.result)
  }
  reader.readAsDataURL(blob)
}

onMounted(() => {
  startRecording()
  recordingInterval = window.setInterval(() => {
    if (mediaRecorder.value && isRecording.value) {
      mediaRecorder.value.stop()
      mediaRecorder.value.start()
    }
  }, 5000)
})

onUnmounted(() => {
  if (mediaRecorder.value) {
    mediaRecorder.value.stop()
    const tracks = mediaRecorder.value.stream.getTracks()
    tracks.forEach(track => track.stop())
  }
  clearInterval(recordingInterval)
})
</script>

<style lang="scss">
.microphone-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #1a1a1a;
  width: 100%;
  height: 100%;
}

.microphone {
  position: relative;
  text-align: center;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.05) 50%, rgba(255, 255, 255, 0.01) 80%, rgba(255, 255, 255, 0.1) 50%, #1a1a1a 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.mic-icon {
  color: #f79c52;
  z-index: 2;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.wave-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.wave {
  width: 200px;
  height: 200px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: wave 2s infinite;
  opacity: 0;

  &:nth-child(2) {
    animation-delay: 0.5s;
  }

  &:nth-child(3) {
    animation-delay: 1s;
  }
}

@keyframes wave {
  0% {
    width: 50px;
    height: 50px;
    opacity: 1;
  }
  100% {
    width: 200px;
    height: 200px;
    opacity: 0;
  }
}

.permission-tip {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  z-index: 1000;
}
</style>