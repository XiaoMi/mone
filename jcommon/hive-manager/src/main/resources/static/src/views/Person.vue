<template>
  <div class="microphone-container">
    <div class="microphone" @click="toggleRecording">
      <div class="mic-icon">
        <el-icon size="50px"><Mic /></el-icon>
      </div>
      <div class="wave-container" v-if="isRecording">
        <div class="wave"></div>
        <div class="wave"></div>
        <div class="wave"></div>
      </div>
    </div>
    <div v-if="!hasPermission" class="permission-tip">
      请允许使用麦克风权限
    </div>
    <div class="chat-container" v-if="chatContent">
      <div class="chat-message">
        <div class="message-content">{{ chatContent }}</div>
        <div class="message-info">
          <span>模型: {{ modelInfo }}</span>
          <span>Token数: {{ tokenCount }}</span>
          <span>ASR时间: {{ asrTime }}ms</span>
        </div>
      </div>
    </div>
    <div class="message-history">
      <div v-for="(message, index) in messageHistory" :key="index"
           :class="['message-item', message.type]">
        <div class="message-time">{{ new Date(message.timestamp).toLocaleTimeString() }}</div>
        <div class="message-content">{{ message.content }}</div>
        <audio v-if="message.audio" :src="message.audio" controls></audio>
      </div>
    </div>
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    <div v-if="statusMessage" class="status-message">
      {{ statusMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { v4 as uuidv4 } from 'uuid';
import { wsUtil } from '@/api/wsUtils';

const mediaRecorder = ref<MediaRecorder | null>(null)
const audioUrls = ref<string[]>([])
const audioChunks = ref<Blob[]>([])
const isRecording = ref(false)
const hasPermission = ref(false)
const socket = ref<WebSocket | null>(null)
const uuid = ref<string>(uuidv4());

const audioAppend = {
  "event_id": uuid.value,
  "type": "input_audio_buffer.append",
  "audio": ""
}

const conversationItem = (audio: string) => ({
  type: 'conversation.item.create',
  item: {
    type: 'message',
    role: 'user',
    status: 'completed',
    content: [
      {
        type: 'input_audio',
        audio: audio
      }
    ]
  }
})

const audioCommit = {
  "event_id": uuid.value,
  "type": "input_audio_buffer.commit",
}

const chatContent = ref('')
const modelInfo = ref('')
const tokenCount = ref(0)
const asrTime = ref(0)
const errorMessage = ref('')
const statusMessage = ref('')
const messageHistory = ref<Array<{
  type: string;
  content: string;
  timestamp: number;
}>>([])

const addMessage = (type: string, content: string, audio?: string) => {
  messageHistory.value.push({
    type,
    content,
    audio: audio || '',
    timestamp: Date.now()
  })
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

const toggleRecording = async () => {
  if (isRecording.value) {
    stopRecording()
  } else {
    await startRecording()
  }
}

const stopRecording = () => {
  if (mediaRecorder.value && isRecording.value) {
    mediaRecorder.value.stop()
    const tracks = mediaRecorder.value.stream.getTracks()
    tracks.forEach(track => track.stop())
    isRecording.value = false

    // 等待最后一个数据块收集完成
    setTimeout(() => {
      if (audioChunks.value.length > 0) {
        sendAudioData()
        // socket.value?.send(JSON.stringify(audioCommit))
        // socket.value?.send(JSON.stringify({type: 'response.create'}))
      }
    }, 100)
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

    isRecording.value = true
    mediaRecorder.value.start()
  } catch (error) {
    console.error('获取麦克风权限失败:', error)
    hasPermission.value = false
  }
}

const sendAudioData = async () => {
  if (audioChunks.value.length === 0) return

  const audioBlob = new Blob(audioChunks.value, { type: 'audio/webm;codecs=pcm' })
  const reader = new FileReader()
  reader.onloadend = () => {
    const base64Data = (reader.result as string).split('base64,')[1]
    const message = conversationItem(base64Data)

    addMessage('audio', '发送音频数据', `data:audio/wav;base64,${base64Data}`);
    // socket.value?.send(JSON.stringify({type: "input_audio_buffer.clear"}))
    socket.value?.send(JSON.stringify(message))
    // socket.value?.send(JSON.stringify(audioCommit))
    socket.value?.send(JSON.stringify({type: 'response.create', status: 'completed'}))
    audioChunks.value = []
  }
  reader.readAsDataURL(audioBlob)
}

const wsConnect = () => {
  if (socket.value) return;

  socket.value = wsUtil("/api/manager/ws/realtime/minimaxi",() => {
    console.log('WebSocket连接成功');
    addMessage('system', 'WebSocket连接成功');
  },() => {
    console.log('WebSocket连接关闭');
    addMessage('system', 'WebSocket连接关闭');
    socket.value = null;
  },async (data: any) => {
    console.log('WebSocket message received:', data);
    if (typeof data === 'string') {
      try {
        data = JSON.parse(data)
      } catch (error) {
        console.error('WebSocket消息解析失败:', error)
      }
    }

    switch (data.type) {
      case 'response.audio.delta':
        // 处理音频数据
        if (data.delta) {
          audioUrls.value.push(`data:audio/wav;base64,${data.delta}`);
          addMessage('audio', '收到音频数据', `data:audio/wav;base64,${data.delta}`);
        }
        break;

      case 'response.audio_transcript.done':
        // 处理语音转文字结果
        if (data.transcript) {
          // chatContent.value = data.transcript;
          addMessage('transcript', data.transcript);
        }
        break;

      case 'response.error':
        // 处理错误消息
        if (data.error) {
          console.error('服务端错误:', data.error);
          errorMessage.value = data.error;
          addMessage('error', data.error);
        }
        break;

      case 'response.status':
        // 处理状态更新
        if (data.status) {
          console.log('服务端状态:', data.status);
          statusMessage.value = data.status;
          addMessage('status', data.status);
        }
        break;

      case 'session.created':
      case 'session.updated':
        if (data.session) {
          addMessage('session', `${data.type}, 会话ID: ${data.session.id}`);
        }
        break;

      default:
        console.log('未处理的消息类型:', data.type);
        addMessage('unknown', `未处理的消息类型: ${data.type}`);
    }
  });
}

onMounted(() => {
  checkMicrophonePermission()
  wsConnect()
})

onUnmounted(() => {
  if (mediaRecorder.value) {
    mediaRecorder.value.stop()
    const tracks = mediaRecorder.value.stream.getTracks()
    tracks.forEach(track => track.stop())
  }
  if (socket.value) {
    socket.value.close()
  }
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
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.05);
  }
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

.chat-container {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  padding: 15px;
  margin-top: 20px;
}

.chat-message {
  color: #fff;
}

.message-content {
  font-size: 16px;
  line-height: 1.5;
  margin-bottom: 10px;
}

.message-info {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);

  span {
    display: inline-block;
  }
}

.message-history {
  position: fixed;
  right: 20px;
  top: 20px;
  width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.8);
  border-radius: 8px;
  padding: 10px;
}

.message-item {
  margin-bottom: 10px;
  padding: 8px;
  border-radius: 4px;

  &.system {
    background: rgba(255, 255, 255, 0.1);
  }

  &.error {
    background: rgba(255, 0, 0, 0.2);
  }

  &.status {
    background: rgba(0, 255, 0, 0.1);
  }

  &.transcript {
    background: rgba(0, 0, 255, 0.1);
  }

  &.audio {
    background: rgba(255, 255, 0, 0.1);
  }
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 4px;
}

.error-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 0, 0, 0.8);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  z-index: 1000;
}

.status-message {
  position: fixed;
  top: 60px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 255, 0, 0.8);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  z-index: 1000;
}
</style>