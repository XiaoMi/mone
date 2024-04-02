<template>
  <div class="recorder" @click="handleStartOrStop">
    <el-icon :color="recording ? 'red' : ''" :size="20"><Mic /></el-icon>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emits = defineEmits(['submit'])

const recording = ref(false)

let recorder: { start(): void; stop(): void } | null = null

async function getRecorder() {
  const mediaStream = navigator.mediaDevices.getUserMedia({ audio: true })

  let chunks: BlobPart[] = []

  return mediaStream.then(
    (stream) => {
      const audioContext = new AudioContext()
      // 创建一个新的音视频对象
      const destination = audioContext.createMediaStreamDestination()
      // 创建音视频源
      const mediaStreamSource = audioContext.createMediaStreamSource(stream)
      // 将音视频源 链接 到新音视频对象 中
      mediaStreamSource.connect(destination)

      // 浏览器原生
      const mediaRecorder = new MediaRecorder(destination.stream, {
        audioBitsPerSecond: 44100,
        bitsPerSecond: 128000
      })

      // 有可用数据流时触发，e.data即需要的音视频数据
      mediaRecorder.ondataavailable = (e: { data: BlobPart }) => {
        chunks.push(e.data)
      }

      // 间视频录制结束时触发
      mediaRecorder.onstop = () => {
        console.log(mediaRecorder.mimeType)
        const blob = new Blob(chunks, { type: mediaRecorder.mimeType })
        // 通过Blob合建对象URL本地地址
        const fileReader = new FileReader()
        fileReader.onload = (e) => {
          const base64Data: string = e.target?.result as string
          if (!base64Data) return
          // console.log(base64Data)
          const url = URL.createObjectURL(blob)
          emits('submit', url, base64Data, blob)
          // const base64String = base64Data.substring(base64Data.indexOf('base64') + 7)
        }
        fileReader.readAsDataURL(blob)
      }

      return {
        start() {
          chunks = []
          mediaRecorder.start()
        },
        stop() {
          mediaRecorder.stop()
        }
      }
    },
    (e) => {
      console.log(e)
      return null
    }
  )
}

async function start() {
  if (recorder == null) {
    recorder = await getRecorder()
  }
  if (recorder == null) {
    return
  }
  console.log('开始录音')
  recording.value = true
  recorder.start()
}

async function stop() {
  console.log('停止录音')
  recording.value = false
  recorder?.stop()
}

function handleStartOrStop() {
  if (!recording.value) {
    start()
  } else {
    stop()
  }
}
</script>

<style>
.recorder {
  display: flex;
  justify-content: center;
  justify-items: center;
}
</style>
