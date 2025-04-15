<template>
  <div class="recorder" @click="handleStartOrStop">
    <el-icon :color="recording ? 'red' : ''" :size="20">
      <Mic />
    </el-icon>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Mp3MediaRecorder } from 'mp3-mediarecorder'
import Mp3RecorderWorker from './worker?worker&inline'

const emits = defineEmits(['submit'])

// 是否正在录音
const recording = ref(false)
// 录音内容
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

      // 媒体录制接口,mp3格式
      const mediaRecorder = new Mp3MediaRecorder(
        destination.stream, // MediaStream instance
        {
          worker: new Mp3RecorderWorker(),
          audioContext: new AudioContext()
        }
      )
      // 浏览器原生
      // const mediaRecorder = new MediaRecorder(destination.stream, {
      //   audioBitsPerSecond: 44100,
      //   bitsPerSecond: 128000
      // })

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
          console.log(base64Data)
          const url = URL.createObjectURL(blob)
          emits('submit', url, base64Data)
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
  recording.value = true
  recorder.start()
}

async function stop() {
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

defineOptions({
  name: 'AudioRecorder'
})
</script>
<style scoped lang="scss">
.recorder {
  padding: 2px 10px;
  display: flex;
  justify-content: center;
  justify-items: center;
}
</style>
