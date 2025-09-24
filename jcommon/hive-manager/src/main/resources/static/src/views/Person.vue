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
        <audio v-if="message.audio" :src="message.audio" controls autoplay></audio>
      </div>
    </div>
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    <div v-if="statusMessage" class="status-message">
      {{ statusMessage }}
    </div>
    <!-- 文本输入框和发送按钮 -->
    <div class="text-input-container">
      <el-input
        v-model="textInput"
        placeholder="请输入文本"
        @keyup.enter="sendText"
        clearable
        style="margin-right: 10px;"
      />
      <el-button type="primary" @click="sendText">发送</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { v4 as uuidv4 } from 'uuid';
import { wsUtil } from '@/api/wsUtils';
import { concatAndEncodeWAV, encodeWAV } from '@/libs/audio'

// MediaRecorder 相关变量移除，改为 Web Audio API 采集 PCM
// const mediaRecorder = ref<MediaRecorder | null>(null)
// const audioChunks = ref<Blob[]>([])
const isRecording = ref(false)
const hasPermission = ref(false)
const socket = ref<WebSocket | null>(null)
const uuid = ref<string>(uuidv4());

// Web Audio API 相关变量
let audioContext: AudioContext | null = null;
let processor: ScriptProcessorNode | null = null;
let input: MediaStreamAudioSourceNode | null = null;
let pcmChunks: Int16Array[] = [];
let streamRef: MediaStream | null = null;

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

const conversationTextItem = (text: string) => ({
  type: 'conversation.item.create',
  item: {
    type: 'message',
    role: 'user',
    status: 'completed',
    content: [
      {
        type: 'input_text',
        text: text
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
  id: string;
  type: string;
  content: string;
  timestamp: number;
  audio: string;
}>>([])

const textInput = ref('')

const addMessage = (id: string, type: string, content: string, audio?: string) => {
  messageHistory.value.push({
    id,
    type,
    content,
    audio: audio || '',
    timestamp: Date.now()
  })
}

const appendMessage = (id: string, type: string, content: string, audio?: string) => {
  const message = messageHistory.value.find(item => item.id === id)
  if (message) {
    message.content += content
    if (audio) {
      message.audio = audio
    }
  } else {
    addMessage(id, type, content, audio)
  }
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

const startRecording = async () => {
  try {
    const permissionGranted = await checkMicrophonePermission()
    if (!permissionGranted) {
      return
    }
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    streamRef = stream;
    audioContext = new (window.AudioContext || window.webkitAudioContext)({ sampleRate: 24000 });
    input = audioContext.createMediaStreamSource(stream);
    processor = audioContext.createScriptProcessor(4096, 1, 1);
    pcmChunks = [];
    processor.onaudioprocess = (e) => {
      const inputData = e.inputBuffer.getChannelData(0); // Float32Array
      // 转成16bit PCM
      const pcm = new Int16Array(inputData.length);
      for (let i = 0; i < inputData.length; i++) {
        let s = Math.max(-1, Math.min(1, inputData[i]));
        pcm[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
      }
      pcmChunks.push(pcm);
    };
    input.connect(processor);
    processor.connect(audioContext.destination);
    isRecording.value = true;
  } catch (error) {
    console.error('获取麦克风权限失败:', error)
    hasPermission.value = false
  }
}

const stopRecording = () => {
  if (!isRecording.value) return;
  isRecording.value = false;
  if (processor && input) {
    processor.disconnect();
    input.disconnect();
  }
  if (audioContext) {
    audioContext.close();
  }
  if (streamRef) {
    streamRef.getTracks().forEach(track => track.stop());
    streamRef = null;
  }
  // 合并所有PCM数据
  const totalLength = pcmChunks.reduce((sum, arr) => sum + arr.length, 0);
  const pcmData = new Int16Array(totalLength);
  let offset = 0;
  for (const chunk of pcmChunks) {
    pcmData.set(chunk, offset);
    offset += chunk.length;
  }
  // 转base64
  const pcmUint8 = new Uint8Array(pcmData.buffer);
  const base64 = btoa(String.fromCharCode(...pcmUint8));
  setTimeout(() => {
    sendAudioData(base64);
    pcmChunks = [];
  }, 0);
}

// sendAudioData 现在接收 base64 PCM
const sendAudioData = async (base64Data: string) => {
  if (!base64Data) return;
  // 这里直接发送 base64 PCM，如果需要WAV可用encodeWAV
  const message = conversationItem(base64Data)
  // console.log('发送音频数据', `data:audio/pcm;base64,${base64Data}`);
  const wavBase64 = await encodeWAV(base64Data, 24000, 1)
  addMessage(uuidv4(), 'audio', '发送音频数据', `data:audio/wav;base64,${wavBase64}`);
  socket.value?.send(JSON.stringify(message))
  socket.value?.send(JSON.stringify({type: 'response.create', status: 'completed'}))
}

const responseAudio = new Map()
const wsConnect = () => {
  if (socket.value) return;

  socket.value = wsUtil("/api/manager/ws/realtime/minimaxi",() => {
    console.log('WebSocket连接成功');
    addMessage(uuidv4(), 'system', 'WebSocket连接成功');
  },() => {
    console.log('WebSocket连接关闭');
    addMessage(uuidv4(), 'system', 'WebSocket连接关闭');
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
      case 'response.created':
      case 'conversation.item.created':
      case 'response.done':
      case 'response.output_item.done':
      case 'response.audio_transcript.done':
        // 不处理
        break;

      case 'response.audio.delta':
        // 处理音频数据
        if (data.delta) {
          const audioBase64 = responseAudio.get(data.response_id) || []
          audioBase64.push(data.delta)
          responseAudio.set(data.response_id, audioBase64)
        }
        break;
      case 'response.audio.done':
        const audioBase64 = responseAudio.get(data.response_id) || []
        responseAudio.delete(data.response_id)
        if (audioBase64 && audioBase64.length > 0) {
          concatAndEncodeWAV(audioBase64, 24000, 1).then(wavBase64 => {
            addMessage(data.response_id, 'audio', '收到音频数据', `data:audio/wav;base64,${wavBase64}`);
          })
        }
        break;

      case 'response.audio_transcript.delta':
        // 处理语音转文字结果
        if (data.delta) {
          appendMessage(data.response_id, 'transcript', data.delta)
        }
        break;

      case 'response.error':
        // 处理错误消息
        if (data.error) {
          console.error('服务端错误:', data.error);
          errorMessage.value = data.error;
          addMessage(data.response_id, 'error', data.error);
        }
        break;

      case 'response.status':
        // 处理状态更新
        if (data.status) {
          console.log('服务端状态:', data.status);
          statusMessage.value = data.status;
          addMessage(data.response_id, 'status', data.status);
        }
        break;

      case 'session.created':
      case 'session.updated':
        if (data.session) {
          addMessage(data.response_id, 'session', `${data.type}, 会话ID: ${data.session.id}`);
        }
        break;

      default:
        console.log('未处理的消息类型:', data.type);
        addMessage(data.response_id, 'unknown', `未处理的消息类型: ${data.type}`);
    }
  });
}

const sendText = () => {
  if (!textInput.value.trim()) return
  const message = conversationTextItem(textInput.value)
  addMessage(uuidv4(), 'text', textInput.value)
  socket.value?.send(JSON.stringify(message))
  socket.value?.send(JSON.stringify({
    type: 'response.create',
    response: {
      status: 'completed'
    }
  }))
  textInput.value = ''
}

onMounted(() => {
  checkMicrophonePermission()
  wsConnect()
})

onUnmounted(() => {
  // 关闭音频流和上下文
  if (isRecording.value) {
    isRecording.value = false;
    if (processor && input) {
      processor.disconnect();
      input.disconnect();
    }
    if (audioContext) {
      audioContext.close();
    }
    if (streamRef) {
      streamRef.getTracks().forEach(track => track.stop());
      streamRef = null;
    }
    pcmChunks = [];
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

.text-input-container {
  display: flex;
  align-items: center;
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  max-width: 600px;
  background: rgba(255,255,255,0.1);
  border-radius: 10px;
  padding: 10px;
  z-index: 1001;
}
</style>