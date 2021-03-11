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
  <div class="list">
      <el-tabs v-model="activeName" tab-position="right" @tab-click="handleClick">
        <el-tab-pane label="Mone入门" name="first">
          <swiper-video :videoList="primaryVideo" ref="primary"/>
        </el-tab-pane>
        <el-tab-pane label="智能网关" name="second">
          <swiper-video :videoList="gatewayVideo" ref="gateway"/>
        </el-tab-pane>
        <el-tab-pane label="应用管理" name="third">
          <swiper-video :videoList="applicationVideo" ref="application"/>
        </el-tab-pane>
        <el-tab-pane label="运维中心" name="fourth">
          <swiper-video :videoList="sreVideo" ref="sre"/>
        </el-tab-pane>
        <el-tab-pane label="配置中心" name="Fifth">
          <swiper-video :videoList="nacosVideo" ref="nacos"/>
        </el-tab-pane>
        <el-tab-pane label="监控中心" name="six">
          <swiper-video :videoList="catVideo" ref="cat"/>
        </el-tab-pane>
        <el-tab-pane label="Filter" name="seven">
          <swiper-video :videoList="filterVideo" ref="filter"/>
        </el-tab-pane>
        <el-tab-pane label="ServiceMesh" name="eight">
          <swiper-video :videoList="serviceMeshVideo" ref="serviceMesh"/>
        </el-tab-pane>
        <el-tab-pane label="Serverless" name="nine">
          <swiper-video :videoList="serverlessVideo" ref="serverless"/>
        </el-tab-pane>
        <el-tab-pane label="其它" name="ten">
          <swiper-video :videoList="otherVideo" ref="other"/>
        </el-tab-pane>
      </el-tabs>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import swiperVideo from './components/swiper-video'
import { filter } from 'minimatch'
export default {
  data () {
    return {
      activeName: 'first',
      primaryVideo: [],
      gatewayVideo: [],
      applicationVideo: [],
      sreVideo: [],
      nacosVideo: [],
      catVideo: [],
      filterVideo: [],
      otherVideo: [],
      serviceMeshVideo: [],
      serverlessVideo: []
    }
  },
  components: {
    swiperVideo
  },
  created () {
    this.getVideoList()
  },
  methods: {
    getVideoList () {
      service({
        url: '/help/video/list'
      }).then(res => {
        const list = res
        if (!Array.isArray(list)) {
          return
        }
        this.primaryVideo = []
        this.gatewayVideo = []
        this.applicationVideo = []
        this.sreVideo = []
        this.nacosVideo = []
        this.catVideo = []
        this.filterVideo = [];
        this.otherVideo = [];
        this.serviceMeshVideo = [];
        this.serverlessVideo = [];
        list.forEach(item => {
          switch (item.tag) {
            case "primary":
              this.primaryVideo.push(item)
              break
            case "gateway":
              this.gatewayVideo.push(item)
              break
            case "application":
              this.applicationVideo.push(item)
              break
            case "sre":
              this.sreVideo.push(item)
              break
            case "nacos":
              this.nacosVideo.push(item)
              break
            case 'cat':
              this.catVideo.push(item)
              break
            case "filter":
              this.filterVideo.push(item);
              break
            case "serviceMesh":
              this.serviceMeshVideo.push(item);
              break;
            case "serverless":
              this.serverlessVideo.push(item);
              break;
            case "other":
              this.otherVideo.push(item)
          }
        })
      })
    },
    handleClick (tab, event) {
      this.primaryVideo.forEach(item => {
        this.$refs.primary.$refs[item.ctime][0].pause()
      })
      this.gatewayVideo.forEach(item => {
        this.$refs.gateway.$refs[item.ctime][0].pause()
      })
      this.applicationVideo.forEach(item => {
        this.$refs.application.$refs[item.ctime][0].pause()
      })
      this.sreVideo.forEach(item => {
        this.$refs.sre.$refs[item.ctime][0].pause()
      })
      this.nacosVideo.forEach(item => {
        this.$refs.nacos.$refs[item.ctime][0].pause()
      })
      this.catVideo.forEach(item => {
        this.$refs.cat.$refs[item.ctime][0].pause()
      })
      this.filterVideo.forEach(item => {
        this.$refs.filter.$refs[item.ctime][0].pause()
      })
      this.otherVideo.forEach(item => {
        this.$refs.other.$refs[item.ctime][0].pause()
      })
      this.serviceMeshVideo.forEach(item => {
        this.$refs.serviceMesh.$refs[item.ctime][0].pause()
      })
      this.serverlessVideo.forEach(item => {
        this.$refs.serverless.$refs[item.ctime][0].pause()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.list {
   width: 1051px;
}
</style>
<style lang="scss">
.list .el-tabs__content {
  width: 861px;
  height: 486px;
}
.list .el-tabs--right .el-tabs__header.is-right {
  width: 190px;
  height: 486px;
  margin-left: 0px;
}
.list .el-tabs__item.is-right {
  width: 191px;
  height: 60px;
  display: flex;
  align-items: center;
  font-family: PingFangSC-Medium;
  font-size: 16px;
  color: #333333;
  line-height: 12px;
}
.list .el-tabs__item.is-right:hover {
  color: #457BFC;
}
.list .el-tabs__item.is-right.is-active {
  background: #F0F4FF;
}
.el-tabs--right .el-tabs__active-bar.is-right {
  left: 1px;
}
.list .el-tabs__active-bar.is-right {
  width: 5.8px;
  height: 60px;
  background-image: url(https://img.youpin.mi-img.com/middlewareGroup/309cdc01a33a4331c0719199bd75d8ae.png?w=18&h=180);
  background-size: cover;
  background-color: #F0F4FF;
}
</style>
