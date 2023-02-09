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
    <div>
      <el-carousel :interval="4000" height="350px" type="card">
        <el-carousel-item v-for="item in newVideoList" :key="item.id">
           <div class="content">
              <video :ref="item.ctime"
                     :controls="item.controls"
                     poster="xx_replace_xx"
                     @pause="videoPause(item)">您的浏览器暂不支持视频播放，请下载最新版chrome ~</video>
              <div class="text">{{ item.description }}</div>
              <div class="btn" @click="handlePlay(item)" v-if="item.showPlay"></div>
              <div class="btn btn-pause" v-if="item.showPause" @click="handlePause(item)"></div>
            </div>
        </el-carousel-item>
      </el-carousel>
    </div>
</template>

<script>
export default {
  props: {
    videoList: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      newVideoList: []
    }
  },
  watch: {
    videoList: function () {
      this.newVideoList = this.videoList.map(item => {
        return {
          ...item,
          showPlay: true,
          controls: false,
          showPause: false
        }
      })
    }
  },
  methods: {
    handlePlay (item) {
      item.showPlay = false
      item.controls = true
      this.$refs[item.ctime][0].src = item.url
      this.$refs[item.ctime][0].play()
    },
    handlePause (item) {
      item.showPause = false
      this.$refs[item.ctime][0].play()
    },
    videoPause (item) {
      item.showPause = true
    }
  }
}
</script>

<style lang="scss" scoped>

.content {
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  position: relative;
  overflow: hidden;
  video {
    width: 100%;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
  }
  .text {
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;
    border: 0.6px solid #ccc;
    padding: 18px;
    text-align: center;
    font-size: 18px;
    color: #202D40;
    position: relative;
    top: -4px;
    z-index: 999;
    background-color: #fff;
  }
  .btn {
      width: 62px;
      height: 62px;
      background-image: url(xx_replace_xx);
      background-size: 100% 100%;
      background-color: rgba(0,0,0,0.30);
      border: 1px solid rgba(255,255,255,0.90);
      border-radius: 50%;
      position: absolute;
      left: 50%;
      top: 38%;
      margin-top: -31px;
      margin-left: -31px;
    }
  .btn-pause {
    background-image: url(xx_replace_xx);
  }
}
</style>
