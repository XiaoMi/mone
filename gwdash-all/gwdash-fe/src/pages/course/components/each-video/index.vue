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
  <div class="each-video">
    <video :ref="newVideoContent.ctime"
           :controls="newVideoContent.controls"
           poster="xx_replace_xx"
           @pause="videoPause(newVideoContent)"></video>
    <div class="text">{{ newVideoContent.description }}</div>
    <div class="icon" title="删除" @click="delVideo(newVideoContent.id)" v-if="isAdmin">
      <i class="el-icon-circle-close"></i>
    </div>
    <div class="btn" @click="handlePlay(newVideoContent)" v-if="newVideoContent.showPlay"></div>
    <div class="btn btn-pause" v-if="newVideoContent.showPause" @click="handlePause(newVideoContent)"></div>
  </div>
</template>

<script>
export default {
  props: {
    videoContent: {
      type: Array,
      required: true
    },
    isAdmin: {
      type: Boolean,
      required: true
    }
  },
  data () {
    return {
      newVideoContent: {}
    }
  },
  watch: {
    videoContent: {
      handler: function () {
        var newVideo = []
        newVideo = this.videoContent.map(item => {
          return {
            ...item,
            showPlay: true,
            controls: false,
            showPause: false
          }
        })
        this.newVideoContent = newVideo[0]
      },
      immediate: true
    }
  },
  methods: {
    handlePlay (item) {
      item.showPlay = false
      item.controls = true
      this.$refs[item.ctime].src = item.url
      this.$refs[item.ctime].play()
    },
    handlePause (item) {
      item.showPause = false
      this.$refs[item.ctime].play()
    },
    videoPause (item) {
      item.showPause = true
    },
    delVideo (id) {
      this.$emit('doDelVideo', id)
    }
  }
}
</script>

<style lang="scss" scoped>
.each-video{
  width: 266px;
  position: relative;
  background: #fff;
  box-shadow: 0px 0px 45px 0px rgba(3, 65, 157, 0.1);
  border-radius: 6px;
  video {
    width: 100%;
    height: 180px;
    border-top-left-radius: 6px;
    border-top-right-radius: 6px;
  }
  .text {
    text-align: center;
    width: 100%;
    height: 38px;
    font-size: 14px;
    line-height: 30px;
    font-weight: 500;
    color: #202D40;
    border-radius: 6px;
  }
  .icon {
    position: absolute;
    right: -8px;
    top: -8px;
    font-size: 20px;
    color: #9e9898;
    cursor: pointer;
    display: none;
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
.each-video:hover .icon {
  display: block;
}
</style>
