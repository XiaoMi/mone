<template>
  <div class="recorder" @click="handleStartOrStop">
    <el-icon :color="recording ? 'red' : ''" :size="20"><Mic /></el-icon>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Mp3MediaRecorder } from 'mp3-mediarecorder'
import Mp3RecorderWorker from './worker?worker&inline'
import { ElMessage } from 'element-plus'

const emits = defineEmits(['submit'])

// 是否正在录音
const recording = ref(false)
// 录音内容
let recorder: { start(): void; stop(): void; isRecording(): boolean } | null = null

const hadleSettingAudio = (extensionId: string) => {
  // 构建设置页面 URL
  const settingsUrl = `chrome://settings/content/siteDetails?site=chrome-extension://${extensionId}`;
  // 打开设置页面
  chrome.tabs.create({ url: settingsUrl });

  ElMessage.warning("请在隐私与安全设置中，设置允许使用麦克风权限")
}

async function getRecorder() {
  try {
    // 检查权限状态
    const permissionStatus = await navigator.permissions.query({ 
      name: 'microphone' as PermissionName 
    });
    
    if (permissionStatus.state === 'denied') {
      hadleSettingAudio(chrome.runtime.id);
      return null;
    }

    const mediaStream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        sampleRate: 44100
      },
      video: false
    });

    let chunks: BlobPart[] = []
    let isRecording = false

    const mediaRecorder = new MediaRecorder(mediaStream, {
      mimeType: 'audio/webm'  // 使用 webm 格式
    })
    
    // 添加错误处理
    mediaRecorder.onerror = (error) => {
      console.error('MediaRecorder 错误:', error)
      ElMessage.error('录音出错，请重试')
    }

    // 添加状态变化监听
    mediaRecorder.onstart = () => {
      console.log('录音开始')
    }

    console.log("start1")

    // 有可用数据流时触发，e.data即需要的音视频数据
    mediaRecorder.ondataavailable = (e: { data: BlobPart }) => {
      console.log('收到音频数据:', e.data)
      chunks.push(e.data)
    }

    // 间视频录制结束时触发
    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'audio/webm' })
      const fileReader = new FileReader()
      fileReader.onload = (e) => {
        const base64Data: string = e.target?.result as string
        if (!base64Data) return
        const url = URL.createObjectURL(blob)
        emits('submit', url, base64Data)
      }
      fileReader.readAsDataURL(blob)
      // 清理资源
      chunks = []
    }

    return {
      start() {
        if (!isRecording) {
          chunks = []
          mediaRecorder.start()
          isRecording = true
        }
      },
      stop() {
        if (isRecording) {
          mediaRecorder.stop()
          isRecording = false
          // 停止所有音轨
          mediaStream.getTracks().forEach(track => track.stop())
        }
      },
      isRecording() {
        return isRecording
      }
    }
  } catch (error) {
    console.error('获取麦克风失败:', error);
    
    if (error instanceof Error) {
      if (error.name === 'NotAllowedError') {
        hadleSettingAudio(chrome.runtime.id);
      } else {
        ElMessage.error({
          message: `录音初始化失败: ${error.message}`,
          duration: 3000
        });
      }
    }
    return null;
  }
}

async function checkMicrophonePermission() {
  try {
    const permissionStatus = await navigator.permissions.query({ 
      name: 'microphone' as PermissionName 
    });
    
    return permissionStatus.state === 'granted';
  } catch (error) {
    console.error('检查麦克风权限失败:', error);
    return false;
  }
}

async function start() {
  try {
    if (!recorder) {
      // 主动请求权限
      await navigator.mediaDevices.getUserMedia({ audio: true });
      const hasPermission = await checkMicrophonePermission();
      if (!hasPermission) {
        hadleSettingAudio(chrome.runtime.id);
        return;
      }
      recorder = await getRecorder();
    }
    
    if (recorder) {
      console.log('开始录音...');
      recording.value = true;
      recorder.start();
    }
  } catch (error) {
    console.error('启动录音失败:', error);
    recording.value = false;
    recorder = null;
    ElMessage.error('启动录音失败，请重试');
  }
}

async function stop() {
  if (recording.value) {
    recording.value = false
    recorder?.stop()
    // 清理recorder
    recorder = null
  }
}

function handleStartOrStop() {
  if (!recording.value) {
    start()
  } else {
    stop()
  }
}
</script>

<style scoped lang="scss">
.recorder {
  padding: 2px 10px;
  display: flex;
  justify-content: center;
  justify-items: center;
}
</style>
