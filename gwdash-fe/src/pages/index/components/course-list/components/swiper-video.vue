<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div class="video-content">
    <el-carousel :interval="5000" height="486px" indicator-position="none" @change="carouselChange">
      <el-carousel-item v-for="item in newVideoList" :key="item.ctime" :name="''+item.ctime">
        <div class="video">
          <video :ref="item.ctime"
                 poster="https://img.youpin.mi-img.com/comment/b774bcbff14adcbfa63b1e922ae6b8f1.png@base@tag=imgScale&m=0&w=861&h=486&c=1"
                 :controls="item.controls"
                 @play='videoWillPlay(item)'
                 @pause="videoPause(item)">
              您的浏览器暂不支持视频播放，请下载最新版chrome ~
          </video>
          <div class="mask" v-if="item.showPlay == true || item.showPause == true">
             <p>{{ item.description }}</p>
          </div>
          <div class="btn" @click="handlePlay(item)" v-if="item.showPlay">
            <span></span>
          </div>
          <div class="btn-pause" v-if="item.showPause" @click="handlePause(item)">
            <div class="cont">
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script>
import qs from 'qs'
export default {
  props: {
    videoList: {
      type: Array,
      required: false
    }
  },
  data () {
    return {
      newVideoList: [],
      videoState: true,
      videoCtime: 0
    }
  },
  watch: {
    videoList: function () {
      this.newVideoList = this.videoList.map(item => {
        return {
          ...item,
          showPlay: true,
          controls: false,
          showPause: false,
          playFlag: true
        }
      })
    }
  },
  methods: {
    handlePlay (item) {
      item.showPlay = false
      if (item.playFlag) {
        item.controls = true
        this.$refs[item.ctime][0].src = item.url
      }
      this.$refs[item.ctime][0].play()
      this.videoState = this.$refs[item.ctime][0].paused
      this.videoCtime = item.ctime
      item.playFlag = false
    },
    videoWillPlay (item) {
      item.showPlay = false
    },
    videoPause (item) {
      item.showPlay = true
      this.videoState = this.$refs[item.ctime][0].paused
      this.videoCtime = item.ctime
    },
    handlePause (item) {
      item.showPause = false
      this.$refs[item.ctime][0].play()
      this.videoState = this.$refs[item.ctime][0].paused
      this.videoCtime = item.ctime
    },
    carouselChange () {
      if (!this.videoState) {
        this.$refs[this.videoCtime][0].pause()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.video-content {
  width: 100%;
  .video {
    width: 100%;
    height: 100%;
    position: relative;
    video {
      width: 100%;
      height: 100%;
    }
    .mask {
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      background-image: radial-gradient(closest-side at 50% 147%, #05061D 48%, #06071E 100%);
      opacity: 0.4;
      p {
        position: absolute;
        bottom: 20px;
        width: 100%;
        text-align: center;
        font-size: 24px;
        color: #a1acbc;
      }
    }
    .btn {
      position: absolute;
      left: 50%;
      top: 50%;
      margin-top: -31px;
      margin-left: -31px;
      width: 62px;
      height: 62px;
      border: 1.5px solid rgba(255,255,255,0.90);
      background-color: rgba(0,0,0,0.30);
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      span {
        display: block;
        width: 0;
        height: 0;
        border-width: 12px 0px 12px 18px;
        border-style: solid;
        margin-left: 6px;
        border-color: transparent transparent transparent #fff;
      }
    }
    .btn-pause {
      position: absolute;
      left: 50%;
      top: 50%;
      margin-top: -31px;
      margin-left: -31px;
      width: 62px;
      height: 62px;
      border: 1.5px solid rgba(255,255,255,0.90);
      background-color: rgba(0,0,0,0.30);
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      .cont {
         width: 18px;
         height: 26px;
         display: flex;
         justify-content: space-between;
         span {
           display: block;
           width: 4px;
           height: 26px;
           background: #fff;
           border-radius: 8px;
         }
      }
    }
  }
}
</style>
