<!--
 * @Description:
 * @Date: 2024-01-19 10:46:31
 * @LastEditTime: 2024-01-25 20:20:00
-->
<template>
  <div class="video-wrap" ref="videoWrapRef">
    <div class="video-container" @click="play">
      <span class="pre" @click="pre"
        ><el-button
          ><el-icon><ArrowLeft /></el-icon></el-button
      ></span>
      <span class="play" v-if="!isPlay" @click="playBtn"
        ><el-button
          ><el-icon><VideoPlay /></el-icon></el-button
      ></span>
      <div
        class="video-content"
        :style="'left:' + left + 'px;transition: all ' + time + 's ease-in-out;'"
      >
        <div class="video-item">
          <video src="../assets/code1.mp4" controls></video>
        </div>
        <div class="video-item">
          <video src="../assets/code2.mp4" controls></video>
        </div>
        <div class="video-item">
          <video src="../assets/code3.mp4" controls></video>
        </div>
        <div class="video-item">
          <video src="../assets/code1.mp4" controls></video>
        </div>
      </div>
      <span class="next" @click="next"
        ><el-button
          ><el-icon><ArrowRight /></el-icon></el-button
      ></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'

const videoWrapRef = ref()
const left = ref(0)
let len = 0
const time = ref(0)
let isPlay = ref(false)

const pre = (event) => {
  const $video = videoWrapRef.value.getElementsByClassName('video-item')
  const width = $video[0].offsetWidth
  if (len === 0) {
    time.value = 0
    len = $video.length - 1
    left.value = -len * width
  }
  let timer = setTimeout(() => {
    len -= 1
    time.value = 0.3
    left.value = -len * width
    clearTimeout(timer)
  }, 0)
  for (let i = 0; i < $video.length - 1; i++) {
    $video[i].querySelector('video').pause()
  }
  isPlay.value = false
  event.stopPropagation()
  return false
}
const next = (event) => {
  len += 1
  const $video = videoWrapRef.value.getElementsByClassName('video-item')
  const width = $video[0].offsetWidth
  time.value = 0.3
  left.value = -len * width
  if (len === $video.length - 1) {
    let timer = setTimeout(() => {
      len = 0
      time.value = 0
      left.value = 0
      clearTimeout(timer)
    }, 300)
  }
  for (let i = 0; i < $video.length - 1; i++) {
    $video[i].querySelector('video').pause()
  }
  isPlay.value = false
  event.stopPropagation()
  return false
}
const play = () => {
  isPlay.value = !isPlay.value
}
const playBtn = () => {
  const $video = videoWrapRef.value.getElementsByClassName('video-item')
  $video[len].querySelector('video').play()
}
nextTick(() => {
  const $video = videoWrapRef.value.getElementsByClassName('video-item')
  for (let i = 0; i < $video.length - 1; i++) {
    $video[i].querySelector('video').addEventListener('pause', function () {
      isPlay.value = false
    })
  }
})
</script>

<style scoped lang="scss">
.video-wrap {
  width: 100%;
  height: 680px;
  text-align: center;
  .video-container {
    width: 1000px;
    height: 100%;
    position: relative;
    margin: 0 auto;
    overflow-x: hidden;
  }
  .video-content {
    width: 400%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }
  .video-item {
    width: 1000px;
    float: left;
    height: 100%;
    display: flex;
    align-items: center;
    position: relative;
    video {
      width: 1000px;
    }
    p {
      position: absolute;
      top: 150px;
      left: 0px;
      width: 100%;
      text-align: center;
      z-index: 4;
      color: #fff;
      font-size: 60px;
      opacity: 0.6;
    }
  }
  .pre,
  .next {
    position: absolute;
    top: 50%;
    margin-top: -20px;
    z-index: 2;
    .oz-button {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: none;
      background-color: rgba(19, 19, 19, 0.5);
      color: #fff;
    }
    .oz-icon {
      font-size: 26px;
    }
    &:hover {
      .oz-button {
        background-color: rgba(19, 19, 19, 1);
      }
    }
  }
  .pre {
    left: 10px;
  }
  .next {
    right: 10px;
  }
  .play {
    position: absolute;
    top: 50%;
    left: 50%;
    margin-top: -40px;
    margin-left: -40px;
    z-index: 2;
    .oz-button {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      border: none;
      background-color: transparent;
      color: #fff;
      opacity: 0.6;
    }
    .oz-icon {
      font-size: 80px;
    }
    &:hover {
      .oz-button {
        background-color: transparent;
      }
      .oz-icon {
        color: #eee;
      }
    }
  }
}
</style>
